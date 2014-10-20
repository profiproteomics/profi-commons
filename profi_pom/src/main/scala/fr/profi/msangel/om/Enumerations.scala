package fr.profi.msangel.om

import fr.profi.msangel.om.implicits.JsonEnumeration

/**
 * Enumerates accepted statuses for MsiTask objects.
 * Values are automatically parsed from/to Json.
 */
object MsiTaskStatus extends JsonEnumeration {

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

  val FAILED = Value("failed")
  val SUCCEEDED = Value("succeeded")
  val KILLED = Value("killed")
}

/**
 * Enumerates MSI collections.
 */
//TODO: rename fields specifically for msangel ?
object MongoDbCollection extends Enumeration {
  val TASK_COLLECTION = Value("task_collection")
  val SEARCH_COLLECTION = Value("search_collection")
  val SEARCH_FORM_COLLECTION = Value("search_form_collection")
  val USER_COLLECTION = Value("user_collection")
}

/**
 * Enumerates accepted search engines.
 */
object SearchEngine extends JsonEnumeration {
  val MASCOT = Value("Mascot")
  val OMSSA = Value("OMSSA")
}

/**
 * Enumerates accepted Mascot search parameters.
 */
object MascotSearchParam extends Enumeration {
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
