package fr.profi.msangel.om

import scala.collection.immutable.HashMap
import fr.profi.util.lang.EnhancedEnum

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

  val MONITORING_ERROR = Value("monitoring_error")

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
  val PAUSED = Value("paused")
  
  val MONITORING_ERROR = Value("monitoring_error")

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

  val MONITORING_ERROR = Value("monitoring_error")
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
  val SERVER_CONFIG_COLLECTION = Value("server_config_collection")

  //val MSANGEL_SERVER_CONFIG_COLLECTION = Value("msangel_server_config_collection")
}

/**
 * Enumerates accepted search engines.
 */
object SearchEngine extends JsonEnumeration {
  val MASCOT = Value("Mascot")
  val OMSSA = Value("OMSSA")
}

/**
 * Enumerates decoy strategies
 */
object DecoyStrategy extends JsonEnumeration {
  val NO_DECOY = Value("No Decoy Database")
  val CONCATENATED = Value("Concatenated Decoy Database")
  val SOFTWARE = Value("Software Decoy")
}

/**
 * Enumerates data file formats as defined in Proline (for import purposes)
 */
object ProlineDataFileFormat extends JsonEnumeration {
  val MASCOT = Value("mascot.dat")
  val OMSSA = Value("omssa.omx")
}


/**
 * Enumerates file conversion tools
 */
object FileConversionTool extends JsonEnumeration {
  val MSCONVERT = Value("ProteoWizard msConvert")
  val MS_DATA_CONVERTER = Value("AB SCIEX MS Data Converter")
  val EXTRACT_MSN = Value("Thermo ExtractMSn")
  val RAW2MZDB = Value("ProFI raw2mzDB")
  val MZDB_ACCESS = Value("ProFI mzdb-access")
}

/**
 * Enumerates handled file extensions.
 */
object DataFileExtension extends JsonEnumeration {

  val RAW, WIFF, TOFTOF, MZDB, MGF = Value

  val MZML, MZXML, MZ5, TEXT, MS1, CMS1, MS2, CMS2 = Value

  val rankedExtensions: Seq[(Int, this.Value)] = Seq(
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

  private def _getInitRank(initExt: this.Value): Int = rankedExtensions.find(_._2 == initExt).map(_._1).getOrElse(0)

  def getLowerExtensions(initExt: this.Value) = {
    val initRank = _getInitRank(initExt)
    rankedExtensions.filter { f => f._1 <= initRank && f._2 != initExt }.map(_._2)
  }
  def getUpperExtensions(initExt: this.Value) = {
    val initRank = _getInitRank(initExt)
    rankedExtensions.filter { f => f._1 >= initRank && f._2 != initExt }.map(_._2)
  }

  def getMinRankedExtensions() = {
    rankedExtensions.filter(_._1 == 1).map(_._2)
  }
  def getMaxRankedExtensions() = {
    val maxRank = rankedExtensions.map(_._1).max
    rankedExtensions.filter(_._1 == maxRank).map(_._2)
  }
  
  def getPrettyName(value: DataFileExtension.Value): String = {
    value match {     
      case RAW => "raw"
      case WIFF => "wiff"
      case TOFTOF => "toftof" // ???
      case MZDB => "mzDB"
      case MGF => "mgf"
        
      case MZML => "mzML"
      case MZXML => "mzXML"      
      case MZ5 => "mz5"
      case TEXT => "txt"
      case MS1 => "ms1"
      case CMS1 => "cms1"
      case MS2 => "ms2"
      case CMS2 => "cms2"
    }
  }
}
/**
 * Enumerates accepted Mascot search parameters.
 */
object MascotSearchParam extends Enumeration {

  def printAll() { println(scala.runtime.ScalaRunTime.stringOf(values)) }

  val ACCESSION = Value
  val CHARGE = Value
  val CLE = Value
  val COM = Value
  val COMP = Value
  val CUTOUT = Value
  val DB = Value
  val DECOY = Value
  val ERRORTOLERANT = Value
  val ErrTolRepeat = Value
  val ETAG = Value
  val FILE = Value
  val FORMAT = Value
  val FORMVER = Value
  val FRAMES = Value
  val INSTRUMENT = Value
  val INTERMEDIATE = Value
  val IT_MODS = Value
  val ITOL = Value
  val ITOLU = Value
  val LOCUS = Value
  val MASS = Value
  val MODS = Value
  val MULTI_SITE_MODS = Value
  val PEAK = Value
  val PEP_ISOTOPE_ERROR = Value
  val PEPMASS = Value
  val PFA = Value
  val PRECURSOR = Value
  val QUANTITATION = Value
  val QUE = Value
  val QUERYLIST = Value
  val RAWFILE = Value
  val RAWSCANS = Value
  val REPORT = Value
  val REPTYPE = Value
  val RTINSECONDS = Value
  val SCANS = Value
  val SEARCH = Value
  val SEG = Value
  val SEQ = Value
  val TAG = Value
  val TAXONOMY = Value
  val TITLE = Value
  val TOL = Value
  val TOLU = Value
  val USER00 = Value
  val USER01 = Value
  val USER02 = Value
  val USER03 = Value
  val USER04 = Value
  val USER05 = Value
  val USER06 = Value
  val USER07 = Value
  val USER08 = Value
  val USER09 = Value
  val USER10 = Value
  val USER11 = Value
  val USER12 = Value
  val USEREMAIL = Value
  val USERNAME = Value
}

/**
 * Enumerates accepted Mascot search parameters.
 */
object OmssaSearchParam extends EnhancedEnum {

  def printAll() { println(scala.runtime.ScalaRunTime.stringOf(values)) }

  val INPUT_FILES = Value("input_files")
  val OUTPUT_FILES = Value("output_files")
  val PRECURSOR_SEARCH_TYPE = Value("precursor_search_type")
  val PRODUCT_SEARCH_TYPE = Value("product_search_type")
  val IONS_TO_SEARCH = Value("ions_to_search")
  val PRECURSOR_TOLERANCE = Value("precursor_tolerance")
  val PRECURSOR_TOLERANCE_UNIT = Value("precursor_tolerance_unit")
  val PRODUCT_TOLERANCE = Value("product_tolerance")
  val ZDEP = Value("zdep")
  val CUTOFF = Value("cutoff")
  val CUTLO = Value("cutlo")
  val SINGLE_WIN = Value("single_win")
  val DOUBLE_WIN = Value("double_win")
  val SINGLE_NUM = Value("single_num")
  val DOUBLE_NUM = Value("double_num")
  val FIXED_PTMS = Value("fixed_ptms")
  val VARIABLE_PTMS = Value("variable_ptms")
  val ENZYME = Value("enzyme")
  val MISSED_CLEAVAGE = Value("missed_cleavage")
  val MIN_NO_ENZYME = Value("min_no_enzyme")
  val MAX_NO_ENZYME = Value("max_no_enzyme")
  val HIT_LIST_LEN = Value("hit_list_len")
  val DATABASE = Value("database")
  val TOP_HIT_NUM = Value("top_hit_num")
  val MIN_SPECTRA = Value("min_spectra")
  val MAX_MODS = Value("max_mods")
  val CHARGE_HANDLING = Value("charge_handling")
  val CALC_PLUS_ONE = Value("calc_plus_one")
  val CALC_CHARGE = Value("calc_charge")
  val MIN_CHARGE = Value("min_charge")
  val MAX_CHARGE = Value("max_charge")
  val CONSIDER_MULT = Value("consider_mult")
  val PLUS_ONE = Value("plus_one")
  val MAX_PRODUCT_CHARGE = Value("max_product_charge")
  val SEARCH_B1 = Value("search_b1")
  val SEARCH_CTERM = Value("search_cterm")
  val MAX_PRODUCTIONS = Value("max_productions")
  val EXACT_MASS = Value("exact_mass")
  val RESEARCH_THRESH = Value("research_thresh")
  val PRECURSOR_CULL = Value("precursor_cull")
  val NO_CORRELATION_SCORE = Value("no_correlation_score")
  val PROB_FOLLOWING_ION = Value("prob_following_ion")
  val N_METHIONINE = Value("n_methionine")
  val NUM_ISOTOPES = Value("num_isotopes")
  val REPORTED_HIT_COUNT = Value("reported_hit_count")

}

/**
 * Enumerates accepted tags in a search form.
 */
object SearchFormTag extends EnhancedEnum {
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
  val WIFF_FILE_PATH = Value("wiff_file_path")
  val MZDB_FILE_PATH = Value("mzdb_file_path")
  val MZML_FILE_PATH = Value("mzml_file_path")
  val PEAKLIST_FILE_PATH = Value("peaklist_file_path")

  val MASCOT_IDENTIFICATION_FILE_PATH = Value("mascot_identification_file_path")
  val OMSSA_IDENTIFICATION_FILE_PATH = Value("omssa_identification_file_path")

  // TODO : move to dedicated place (WorkflowJob ? )
  def getFormatKey(fileFormat: DataFileExtension.Value): this.Value = {
    import DataFileExtension._
    fileFormat match {
      case RAW  => RAW_FILE_PATH
      case WIFF => WIFF_FILE_PATH
      case MZDB => MZDB_FILE_PATH
      case MZML => MZML_FILE_PATH
      case MGF  => PEAKLIST_FILE_PATH
     /* case MZXML =>
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
  
  def getFormatKeyAsString (fileFormat: DataFileExtension.Value): String = {
    this.getFormatKey(fileFormat).toString()
  }
}