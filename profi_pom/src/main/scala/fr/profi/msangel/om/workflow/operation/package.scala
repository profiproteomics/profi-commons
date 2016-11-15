package fr.profi.msangel.om.workflow

import org.cvogt.play.json.implicits.optionNoError

import scala.collection.mutable.HashMap

import play.api.data.validation.ValidationError
import play.api.libs.json._

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._
import fr.profi.msangel.om.workflow.operation.conversion.MsDataConverter

import julienrf.variants.Variants

package object operation {

  /*
   * **************************************************************************** *
   * Reads and writes for basic types, that are not handled by play.api.libs.json *
   * **************************************************************************** *
   */

  /** JSON reader for Tuple2 **/
  //from https://gist.github.com/alexanderjarvis/4595298
  /*implicit def tuple2Reads[A, B](implicit aReads: Reads[A], bReads: Reads[B]): Reads[Tuple2[A, B]] = Reads[Tuple2[A, B]] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr(0))
      b <- bReads.reads(arr(1))
    } yield (a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  /** JSON writer for Tuple2 **/
  implicit def tuple2Writes[A, B](implicit aWrites: Writes[A], bWrites: Writes[B]): Writes[Tuple2[A, B]] = new Writes[Tuple2[A, B]] {
    def writes(tuple: Tuple2[A, B]) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
  }*/
  
  // From https://github.com/hmrc/simple-reactivemongo/blob/master/src/main/scala/uk/gov/hmrc/mongo/json/TupleFormats.scala
  implicit def tuple2Reads[B, T1, T2](c : (T1, T2) => B)(implicit aReads: Reads[T1], bReads: Reads[T2]): Reads[B] = Reads[B] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr(0))
      b <- bReads.reads(arr(1))
    } yield c(a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  implicit def tuple2Writes[T1, T2](implicit aWrites: Writes[T1], bWrites: Writes[T2]): Writes[Tuple2[T1, T2]] = new Writes[Tuple2[T1, T2]] {
    def writes(tuple: Tuple2[T1, T2]) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
  }
  
  implicit def tuple2Format[T1, T2](implicit aReads: Reads[T1], bReads: Reads[T2], aWrites: Writes[T1], bWrites: Writes[T2]) = {
    Format(tuple2Reads[Tuple2[T1, T2], T1, T2]((t1, t2) => (t1, t2)), tuple2Writes[T1, T2])
  }

  /** JSON Format for HashMap[String, String] **/
  implicit val stringMapFormat: Format[HashMap[String, String]] = new Format[HashMap[String, String]] {

    /** JSON reader for HashMap[String, String] **/
    // From http://stackoverflow.com/questions/19974014/how-to-deserialize-a-map-of-map-with-play
    def reads(jsValue: JsValue): JsResult[HashMap[String, String]] = {
      //JsSuccess(HashMap("val1" -> (jv \ "val1").as[String], "val2" -> (jv \ "val2").as[String]))
      val hashMap = HashMap[String, String]()
      
      jsValue.as[Map[String, JsValue]].foreach{ case (k, v) =>
        k -> (v match {
          case s: JsString => hashMap += k -> s.as[String]
          case _ => throw new Exception("can only read HashMap[String, String]")
        })
      }
      
      JsSuccess(hashMap)
    }
    
    /** JSON writer for HashMap[String, String] **/
    def writes(hashMap: HashMap[String, String]): JsValue = {
      //OPT 1 : Json.toJson(hashMap.toMap)
      //OPT 2 : val fields = hashMap.filter(_._2.nonEmpty).map(t => (t._1.toString(), Json.toJson(t._2))).toSeq
      //        JsObject(fields)

      val strTuples = for( (k,v) <- hashMap.toSeq ) yield k -> JsString(v)

      JsObject(strTuples)
    }
  } // EOF stringMapFormat
  
  /*
   * *************************************************** *
   * JSON Formats for case classes (related to workflow) *
   * *************************************************** *
   */

  implicit val macroParamFormat: Format[MacroParam] = Json.format[MacroParam]
  implicit val macroChoiceParamItemFormat: Format[MacroChoiceParamItem] = Json.format[MacroChoiceParamItem]
  implicit val macroSelectionParamItemFormat: Format[MacroSelectionParamItem] = Json.format[MacroSelectionParamItem]
  implicit val macroFilterParamFormat: Format[MacroFilterParam] = Json.format[MacroFilterParam]
  implicit val conversionToolConfigFormat: Format[ConversionToolConfig] = Json.format[ConversionToolConfig]
  
  implicit val cmdLineExecutionFormat: Format[CmdLineExecution] = Json.format[CmdLineExecution]
  implicit val emailNotificationFormat: Format[EMailNotification] = Json.format[EMailNotification]
  implicit val webServiceCallFormat: Format[WebServiceCall] = Json.format[WebServiceCall]

  /**
   * ****************************************** *
   * Abstract model for all workflow operations *
   * ****************************************** *
   */
  
  sealed trait IWorkflowOperation {

    val isJobOperation: Boolean

    val emailNotification: Option[EMailNotification]
    val cmdLineExecution: Option[CmdLineExecution]
    val webServiceCall: Option[WebServiceCall]

    /** Clone this workflow job operation **/
    def cloneMe(): IWorkflowOperation
  }

  /**
   * ***************************************************************************************** *
   * Abstract model for all workflow operations applying on each job of the task independently *
   * ***************************************************************************************** *
   */
  sealed trait IWorkflowJobOperation { this: IWorkflowOperation =>
    val isJobOperation: Boolean = true
  }

  /**
   * ************************************************************************************* *
   * Abstract model for all workflow operations applying on alls jobs of the task together *
   * ************************************************************************************* *
   */
  sealed trait IWorkflowTaskOperation { this: IWorkflowOperation =>
    var status: TaskStatus.Value
    val isJobOperation: Boolean = false
    
    val emailNotification: Option[EMailNotification] = None
    val cmdLineExecution: Option[CmdLineExecution] = None
    val webServiceCall: Option[WebServiceCall] = None
  }
  
  /* ***************************************************************************************************************************************************************************
   * ***************************************************************************************************************************************************************************
   * 
   * 																																		JOB OPERATIONS
   * 
   * ***************************************************************************************************************************************************************************
   *************************************************************************************************************************************************************************** */

  /**
   * ************************** *
   * Model for a FileConversion *
   * ************************** *
   */
  case class FileConversion(
    inputFileFormat: DataFileExtension.Value,
    outputFileFormat: DataFileExtension.Value,
    config: ConversionToolConfig,
    useProlineRule: Boolean = false, //TODO: move to config?
    outputDirectory: String,
    @deprecated("0.5.0","don't use this option, it should be always true and it will be removed in the future") overwriteOutputFile: Boolean = true,
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None
  ) extends IWorkflowOperation with IWorkflowJobOperation {
  
    require(inputFileFormat != null, "Initial format must be specified")
    require(outputFileFormat != null, "Target format must be specified")
    require(config != null, "Conversion tool config must be specified")
    require(outputDirectory != null && outputDirectory.isEmpty() == false, "Output directory must be specified")
    
    /** Clone this FileConversion **/
    def cloneMe() = this.copy()
    
    /** Assess if profile mode is PeakPicking **/
    //TODO: move to right place
    def someProfilePeakPicking(): Boolean = {
      for (
        p <- this.config.params;
        //if p.isInstanceOf[MacroSelectionParam];
        if p.name == MsDataConverter.ParamName.PEAK_PICKING
      ) {
        if (p.value == Some(MsDataConverter.profile)) return true
        else return false
      }
      false
    }
  } // ends FileConversion
  
  
  
  /**
   * **************************** *
   * Model for a MzdbRegistration *
   * **************************** *
   */
  case class MzdbRegistration(
    instrumentId: Long, //uds ID
  
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None
  ) extends IWorkflowOperation with IWorkflowJobOperation {
  
    require(instrumentId > 0, "Invalid instrument ID for MzdbRegistration")
  
    /** Clone this MzdbRegistration **/
    def cloneMe() = this.copy()
  } // EOF MzdbRegistration
  
  
  
  /**
   * ************************ *
   * Model for a FileTransfer *
   * ************************ *
   */
  case class FileTransfer(
    initFolder: String,
    targetFolder: String,
  
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None
  ) extends IWorkflowOperation with IWorkflowJobOperation {
  
    /** Clone this FileTransfer **/
    def cloneMe() = this.copy()
  } // EOF FileTransfer
  
  
  
  /**
   * ********************************** *
   * Model for a PeaklistIdentification *
   * ********************************** *
   */
  case class PeaklistIdentification(
    var searchEnginesWithFormIds: Array[(SearchEngine.Value, Option[String])],
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None
  ) extends IWorkflowOperation with IWorkflowJobOperation {
  
    /** Clone this PeaklistIdentification **/
    def cloneMe() = this.copy()
  
    /** Check is some search has no MsiSearchForm **/
    def someSearchWithoutTemplate() = !this.searchEnginesWithFormIds.map(_._2).forall(_.isDefined)
  } // EOF PeaklistIdentification
  
  
  
  /**
   * ************************* *
   * Model for a ProlineImport *
   * ************************* *
   */
  case class ProlineImport(
    instrumentConfigId: Long, //uds ID
    peaklistSoftwareId: Long, //uds ID
    decoyStrategy: DecoyStrategy.Value = DecoyStrategy.SOFTWARE,
    format: ProlineDataFileFormat.Value = ProlineDataFileFormat.MASCOT,
    protMatchDecoyRuleId: Option[Long] = None, //uds ID
    importerProperties: HashMap[String, String] = HashMap(),
    
    autoMapRawFiles: Boolean = true,
  
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None
  ) extends IWorkflowOperation with IWorkflowJobOperation {
  
    require(instrumentConfigId > 0, "Invalid instrumentConfig ID for ProlineImport")
    require(peaklistSoftwareId > 0, "Invalid peaklistSoftware ID for ProlineImport")
  
    /** Clone this ProlineImport **/
    def cloneMe() = this.copy()
  } // end of ProlineImport

  /* ***************************************************************************************************************************************************************************
   * ***************************************************************************************************************************************************************************
   * 
   * 																																		TASK OPERATIONS
   * 
   * ***************************************************************************************************************************************************************************
   *************************************************************************************************************************************************************************** */

  /**
   * ********************** *
   * Model for a Validation *
   * ********************** *
   */
  /*
  //case class Validation(config: ValidationConfig) extends IWorkflowTaskOperation {
  case class Validation(config: JsObject) extends IWorkflowOperation with IWorkflowTaskOperation {

    //require(config != null, "Validation config must be specified")

    /** Clone this Validation **/
    def cloneMe() = this.copy()

  } // ends Validation
  */

  /**
   * ************************ *
   * Model for a Quantitation *
   * ************************ *
   */
  //case class Quantitation(config: QuantitationConfig) extends IWorkflowTaskOperation {
  case class Quantitation(
    var status: TaskStatus.Value = TaskStatus.PENDING,
    val name: String,
    val config: JsObject
  ) extends IWorkflowOperation with IWorkflowTaskOperation {

    //require(config != null, "Quantitation config must be specified")

    /** Clone this Quantitation **/
    def cloneMe() = this.copy()

  } // ends Quantitation


  /* JSON Format for any workflow operation */

  // TODO: try to upgrade JSON Variants (I hope you like to drink hot coffee)
  //implicit val workflowOperationFormat: OFormat[IWorkflowOperation] = derived.oformat //((__ \ "type").format[String]) //[IWorkflowOperation] //("type")
  implicit val workflowOperationFormat: Format[IWorkflowOperation] = Variants.format[IWorkflowOperation]("type")
}