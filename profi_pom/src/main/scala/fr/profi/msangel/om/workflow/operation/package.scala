package fr.profi.msangel.om.workflow

import scala.collection.mutable.HashMap

import fr.profi.msangel.om.workflow.operation.conversion.MsDataConverter

package object operation {

  import java.io.File
  
  import play.api.data.validation.ValidationError
  import play.api.libs.json._
  
  import julienrf.variants.Variants
  
  import fr.profi.msangel.om._
  import fr.profi.msangel.om.DataFileExtension
  import fr.profi.msangel.om.SearchEngine

  /*
   * **************************************************************************** *
   * Reads and writes for basic types, that are not handled by play.api.libs.json *
   * **************************************************************************** *
   */

  /** JSON reader for Tuple2 **/
  //from https://gist.github.com/alexanderjarvis/4595298
  implicit def tuple2Reads[A, B](implicit aReads: Reads[A], bReads: Reads[B]): Reads[Tuple2[A, B]] = Reads[Tuple2[A, B]] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr(0))
      b <- bReads.reads(arr(1))
    } yield (a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  /** JSON writer for Tuple2 **/
  implicit def tuple2Writes[A, B](implicit aWrites: Writes[A], bWrites: Writes[B]): Writes[Tuple2[A, B]] = new Writes[Tuple2[A, B]] {
    def writes(tuple: Tuple2[A, B]) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
  }

  /** JSON Format for HashMap[String, String] **/
  implicit val objectMapFormat = new Format[HashMap[String, String]] {

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
  } // EOF objectMapFormat

  /*
   * *************************************************** *
   * JSON Formats for case classes (related to workflow) *
   * *************************************************** *
   */

  implicit val macroChoiceParamItemFormat = Json.format[MacroChoiceParamItem]
  implicit val macroParamFormat = Json.format[MacroParam]
  implicit val macroFilterParamFormat = Json.format[MacroFilterParam]
  implicit val conversionToolConfigFormat = Json.format[ConversionToolConfig]

  implicit val cmdLineExecutionFormat = Json.format[CmdLineExecution]
  implicit val emailNotificationFormat = Json.format[EMailNotification]
  implicit val webServiceCallFormat = Json.format[WebServiceCall]

  
  
  /**
   * ****************************************** *
   * Abstract model for all workflow operations *
   * ****************************************** *
   */
  sealed trait IWorkflowOperation {
    var executed: Boolean = false
    val emailNotification: Option[EMailNotification]
    val cmdLineExecution: Option[CmdLineExecution]
    val webServiceCall: Option[WebServiceCall]

    /** Clone this workflow operation **/
    def cloneMe(): IWorkflowOperation
  }

  
  
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
  ) extends IWorkflowOperation {

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
  } // EOF FileConversion

  
  
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
  ) extends IWorkflowOperation {

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
  ) extends IWorkflowOperation {

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
  ) extends IWorkflowOperation {

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

    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None
  ) extends IWorkflowOperation {

    require(instrumentConfigId > 0, "Invalid instrumentConfig ID for ProlineImport")
    require(peaklistSoftwareId > 0, "Invalid peaklistSoftware ID for ProlineImport")

    /** Clone this ProlineImport **/
    def cloneMe() = this.copy()
  } // EOF ProlineImport

  /* JSON Format for any workflow operation */
  implicit val workflowOperationFormat: Format[IWorkflowOperation] = Variants.format[IWorkflowOperation]("type")
}