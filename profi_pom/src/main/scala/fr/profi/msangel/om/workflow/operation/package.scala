package fr.profi.msangel.om.workflow

import scala.collection.mutable.HashMap
import fr.profi.msangel.om.MsiSearchForm

package object operation {

  import java.io.File
  import play.api.data.validation.ValidationError
  import play.api.libs.json._
  import julienrf.variants.Variants
  import fr.profi.msangel.om._
  import fr.profi.msangel.om.DataFileFormat
  import fr.profi.msangel.om.SearchEngine

  /**
   * Reads and writes for basic types, that are not handled by play.api.libs.json
   */

  /** For Tuple2 */
  //from https://gist.github.com/alexanderjarvis/4595298
  implicit def tuple2Reads[A, B](implicit aReads: Reads[A], bReads: Reads[B]): Reads[Tuple2[A, B]] = Reads[Tuple2[A, B]] {
    case JsArray(arr) if arr.size == 2 => for {
      a <- aReads.reads(arr(0))
      b <- bReads.reads(arr(1))
    } yield (a, b)
    case _ => JsError(Seq(JsPath() -> Seq(ValidationError("Expected array of two elements"))))
  }

  implicit def tuple2Writes[A, B](implicit aWrites: Writes[A], bWrites: Writes[B]): Writes[Tuple2[A, B]] = new Writes[Tuple2[A, B]] {
    def writes(tuple: Tuple2[A, B]) = JsArray(Seq(aWrites.writes(tuple._1), bWrites.writes(tuple._2)))
  }

  /**
   * Formats for case classes (related to workflow)
   */
  //  implicit val macroBooleanParamFormat = Json.format[MacroBooleanParam]
  //  implicit val macroStringParamFormat = Json.format[MacroStringParam]
  //  implicit val macroNumericParamFormat = Json.format[MacroNumericParam]
  //  implicit val macroRangeParamFormat = Json.format[MacroRangeParam]
  //  implicit val cmdLineGeneratorFormat = Json.format[CmdLineGenerator]
  implicit val macroChoiceParamItemFormat = Json.format[MacroChoiceParamItem]
  implicit val macroParamFormat = Json.format[MacroParam]
  //  implicit val seqMacroStringParamFormat = Json.format[Seq[MacroStringParam]]
  //  implicit val macroParamFormat: Format[MacroParam] = Variants.format[MacroParam]("type")

  implicit val macroFilterParamFormat = Json.format[MacroFilterParam]
  implicit val conversionToolConfigFormat = Json.format[ConversionToolConfig]

  implicit val cmdLineExecutionFormat = Json.format[CmdLineExecution]
  implicit val emailNotificationFormat = Json.format[EMailNotification]
  implicit val webServiceCallFormat = Json.format[WebServiceCall]

  sealed trait IWorkflowOperation {
    var executed: Boolean = false
    val emailNotification: Option[EMailNotification]
    val cmdLineExecution: Option[CmdLineExecution]
    val webServiceCall: Option[WebServiceCall]
  }

  case class FileConversion(
    inputFileFormat: DataFileFormat.Value,
    outputFileFormat: DataFileFormat.Value,
    config: ConversionToolConfig, // TODO: replace by conversionToolId
    outputDirectory: String,
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None) extends IWorkflowOperation {

    require(inputFileFormat != null, "Initial format must be specified")
    require(outputFileFormat != null, "Target format must be specified")
    require(config != null, "Conversion tool config must be specified")
    require(outputDirectory != null && outputDirectory.isEmpty() == false, "Output directory must be specified")
  }

  case class FileTransfer(
    initFolder: String,
    targetFolder: String,
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None) extends IWorkflowOperation

  case class PeaklistIdentification(
    searchEngines: Array[SearchEngine.Value],
    // searchForms: Array[MsiSearchForm],
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None) extends IWorkflowOperation //MsiSearch

  case class ProlineImport(
    ownerMongoId: String, //mongo ID
    projectId: Long, //uds ID
    instrumentConfigId: Long, //uds ID
    peaklistSoftwareId: Long, //uds ID
    emailNotification: Option[EMailNotification] = None,
    cmdLineExecution: Option[CmdLineExecution] = None,
    webServiceCall: Option[WebServiceCall] = None) extends IWorkflowOperation {

    require(ownerMongoId != null, "Task's owner mongo ID must not be null") //TODO : hexaDec + size
    require(projectId > 0, "Invalid project ID for ProlineImport")
    require(instrumentConfigId > 0, "Invalid instrumentConfig ID for ProlineImport")
    require(peaklistSoftwareId > 0, "Invalid peaklistSoftware ID for ProlineImport")
  }

  implicit val workflowOperationFormat: Format[IWorkflowOperation] = Variants.format[IWorkflowOperation]("type")

}