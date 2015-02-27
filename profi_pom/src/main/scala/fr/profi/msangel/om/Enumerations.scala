package fr.profi.msangel.om

import scala.collection.immutable.HashMap

/**
 * Enumerates accepted statuses for WorkflowTask and MsiTask objects.
 * Values are automatically parsed from/to Json.
 */
object TaskStatus extends JsonEnumeration {

  val CREATED = Value("created")

  val UPLOADING = Value("uploading")
  val PENDING = Value("pending")
  //  val QUEUED = Value("queued")

  val RUNNING = Value("running")
  val PAUSED = Value("paused")

  val DELETED = Value("deleted") //TODO: by owner only. Change into "killed"?
  val FAILED = Value("failed")
  val SUCCEEDED = Value("succeeded")
}

/*
trait TaskStatus extends JsonEnumeration {
  //  val CREATED = Value("created")
  //  val RUNNING = Value("running")
  //  val FAILED = Value("failed")
  //  val SUCCEEDED = Value("succeeded")
}

/**
 * Enumerates accepted statuses for WorkflowTask objects.
 * Values are automatically parsed from/to Json.
 */
object WorkflowStatus extends TaskStatus {
  //  val CREATED = Value("created")
  //  val RUNNING = Value("running")
  //  val FAILED = Value("failed")
  //  val SUCCEEDED = Value("succeeded")
}

/**
 * Enumerates accepted statuses for MsiTask objects.
 * Values are automatically parsed from/to Json.
 */
object MsiTaskStatus extends TaskStatus {

  //  val CREATED = Value("created")

  val UPLOADING = Value("uploading")
  val PENDING = Value("pending")
  //  val QUEUED = Value("queued")

  //  val RUNNING = Value("running")
  val PAUSED = Value("paused")

  val DELETED = Value("deleted") //TODO: by owner only. Change into "killed"?
  //  val FAILED = Value("failed")
  //  val SUCCEEDED = Value("succeeded")
}
*/

/**
 * Enumerates accepted statuses for WorkflowJob objects.
 * Values are automatically parsed from/to Json.
 */
object WorkflowJobStatus extends JsonEnumeration {
  val CREATED = Value("created")
  val RUNNING = Value("running")

  val FAILED = Value("failed")
  val SUCCEEDED = Value("succeeded")
}

/**
 * Enumerates accepted statuses for MsiSearch objects.
 * Values are automatically parsed from/to Json.
 */
object MsiSearchStatus extends JsonEnumeration {

  val CREATED = Value("created")
  val UPLOADING = Value("uploading")
  val PENDING = Value("pending")

  val RUNNING = Value("running")
  val PAUSED = Value("paused")

  //  val IMPORTING = Value("importing") //into Proline. TODO : rename?

  val FAILED = Value("failed")
  val SUCCEEDED = Value("succeeded")
  val KILLED = Value("killed")
}

/**
 * Enumerates Mongo collections used in MSAngel.
 */
//TODO: rename fields specifically for msangel ?
object MongoDbCollection extends Enumeration {

  val WORKFLOW_TASK_COLLECTION = Value("workflow_task_collection")
  val WORKFLOW_JOB_COLLECTION = Value("workflow_job_collection")
  val WORKFLOW_COLLECTION = Value("workflow_collection")

  val MSI_TASK_COLLECTION = Value("msi_task_collection")
  val MSI_SEARCH_COLLECTION = Value("msi_search_collection")
  val MSI_SEARCH_FORM_COLLECTION = Value("msi_search_form_collection")

  val USER_COLLECTION = Value("user_collection")

  //val MSANGEL_SERVER_CONFIG_COLLECTION = Value("msangel_server_config_collection")
}

/**
 * Enumerates accepted search engines.
 */
object SearchEngine extends JsonEnumeration {
  val MASCOT = Value("Mascot")
  val OMSSA = Value("OMSSA")
}

object FileConversionTool extends JsonEnumeration {
  val MSCONVERT = Value("ProteoWizard msConvert")
  val EXTRACT_MSN = Value("Thermo ExtractMSn")
  val RAW2MZDB = Value("ProFI raw2mzDB")
  val MZDB_ACCESS = Value("ProFI mzdb-access")

  implicit def enum2string(tool: FileConversionTool.Value): String = tool.toString()
}

/**
 * Enumerates handled file extensions.
 */
object DataFileFormat extends JsonEnumeration { //TODO : rename into extension?

  val RAW = Value("RAW")
  val WIFF = Value("WIFF")
  val MZDB = Value("MZDB")
  val MGF = Value("MGF")

  val MZML = Value("MZML")
  val MZXML = Value("MZXML")
  val MZ5 = Value("MZ5")
  val TEXT = Value("TEXT")
  val MS1 = Value("MS1")
  val CMS1 = Value("CMS1")
  val MS2 = Value("MS2")
  val CMS2 = Value("CMS2")

  val rankedFormats: Seq[(Int, this.Value)] = Seq(
    (1, this.RAW),
    (1, this.WIFF),
    (2, this.MZDB),
    (3, this.MGF),

    (3, this.MZML),
    (3, this.MZXML),
    (3, this.MZ5),
    (3, this.TEXT),
    (3, this.MS1),
    (3, this.CMS1),
    (3, this.MS2),
    (3, this.CMS2)
  )

  private def _getInitRank(initFormat: this.Value): Int = rankedFormats.find(_._2 == initFormat).map(_._1).getOrElse(0)

  def getLowerFormats(initFormat: this.Value) = {
    val initRank = _getInitRank(initFormat)
    rankedFormats.filter { f => f._1 <= initRank && f._2 != initFormat }.map(_._2)
  }
  def getUpperFormats(initFormat: this.Value) = {
    val initRank = _getInitRank(initFormat)
    rankedFormats.filter { f => f._1 >= initRank && f._2 != initFormat }.map(_._2)
  }

  def getMinRankedFormats() = {
    rankedFormats.filter(_._1 == 1).map(_._2)
  }
  def getMaxRankedFormats() = {
    val maxRank = rankedFormats.map(_._1).max
    rankedFormats.filter(_._1 == maxRank).map(_._2)
  }
}
/**
 * Enumerates accepted Mascot search parameters.
 */
object MascotSearchParam extends Enumeration {

  def printAll() { println(scala.runtime.ScalaRunTime.stringOf(values)) }
  //  def contains(s: String): Boolean = values.exists(_.toString == s)

  val ACCESSION = Value("ACCESSION")
  val CHARGE = Value("CHARGE")
  val CLE = Value("CLE")
  val COM = Value("COM")
  val COMP = Value("COMP")
  val CUTOUT = Value("CUTOUT")
  val DB = Value("DB")
  val DECOY = Value("DECOY")
  val ERRORTOLERANT = Value("ERRORTOLERANT")
  val ErrTolRepeat = Value("ErrTolRepeat")
  val ETAG = Value("ETAG")
  val FILE = Value("FILE")
  val FORMAT = Value("FORMAT")
  val FORMVER = Value("FORMVER")
  val FRAMES = Value("FRAMES")
  val INSTRUMENT = Value("INSTRUMENT")
  val INTERMEDIATE = Value("INTERMEDIATE")
  val IT_MODS = Value("IT_MODS")
  val ITOL = Value("ITOL")
  val ITOLU = Value("ITOLU")
  val LOCUS = Value("LOCUS")
  val MASS = Value("MASS")
  val MODS = Value("MODS")
  val MULTI_SITE_MODS = Value("MULTI_SITE_MODS")
  val PEAK = Value("PEAK")
  val PEP_ISOTOPE_ERROR = Value("PEP_ISOTOPE_ERROR")
  val PEPMASS = Value("PEPMASS")
  val PFA = Value("PFA")
  val PRECURSOR = Value("PRECURSOR")
  val QUANTITATION = Value("QUANTITATION")
  val QUE = Value("QUE")
  val QUERYLIST = Value("QUERYLIST")
  val RAWFILE = Value("RAWFILE")
  val RAWSCANS = Value("RAWSCANS")
  val REPORT = Value("REPORT")
  val REPTYPE = Value("REPTYPE")
  val RTINSECONDS = Value("RTINSECONDS")
  val SCANS = Value("SCANS")
  val SEARCH = Value("SEARCH")
  val SEG = Value("SEG")
  val SEQ = Value("SEQ")
  val TAG = Value("TAG")
  val TAXONOMY = Value("TAXONOMY")
  val TITLE = Value("TITLE")
  val TOL = Value("TOL")
  val TOLU = Value("TOLU")
  val USER00 = Value("USER00")
  val USER01 = Value("USER01")
  val USER02 = Value("USER02")
  val USER03 = Value("USER03")
  val USER04 = Value("USER04")
  val USER05 = Value("USER05")
  val USER06 = Value("USER06")
  val USER07 = Value("USER07")
  val USER08 = Value("USER08")
  val USER09 = Value("USER09")
  val USER10 = Value("USER10")
  val USER11 = Value("USER11")
  val USER12 = Value("USER12")
  val USEREMAIL = Value("USEREMAIL")
  val USERNAME = Value("USERNAME")
}

/**
 * Enumerates accepted tags in a search form.
 */
object SearchFormTag extends Enumeration {
  val PROLINE_PROJECT = Value("<proline_project>")
  val PROLINE_USER = Value("<proline_user>")
  val TASK_NAME = Value("<task_name>")
  val INPUT_FILE_NAME = Value("<input_file_name>")
  val INPUT_FILE_PATH = Value("<input_file_path>")
  //TODO: add <parameters>, <localhost>, <localuser> and Mascot Security-related tags.
}

/**
 * Enumerates MSI tasks' scheduling types
 */
object SchedulingType extends JsonEnumeration {
  val START_NOW = Value("Start now")
  val REAL_TIME_MONITORING = Value("Real time monitoring")
  val START_AT = Value("Start at")
  val ADD_TO_QUEUE = Value("Add to queue")
}

/**
 *  Enumerates WorkflowJob executionVariables' map keys
 */
object ExecutionVariable extends JsonEnumeration {
  val RAW_FILE_PATH = Value("raw_file_path")
  val MZDB_FILE_PATH = Value("mzdb_file_path")
  val PEAKLIST_FILE_PATH = Value("peaklist_file_path")

  val MASCOT_IDENTIFICATION_FILE_PATH = Value("mascot_identification_file_path")
  val OMSSA_IDENTIFICATION_FILE_PATH = Value("omssa_identification_file_path")

  def apply(fileFormat: DataFileFormat.Value): this.Value = {
    import DataFileFormat._
    fileFormat match {
      case RAW   => RAW_FILE_PATH
      case MZDB  => MZDB_FILE_PATH
      case MGF   => PEAKLIST_FILE_PATH
     /* case WIFF  =>
      case MZML  =>
      case MZXML =>
      case MZ5   =>
      case TEXT  =>
      case MS1   =>
      case CMS1  =>
      case MS2   =>
      case CMS2  =>
      */
      case _ => throw new Exception("File extension not handled")
    }
  }
}