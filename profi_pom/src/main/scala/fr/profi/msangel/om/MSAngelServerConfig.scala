package fr.profi.msangel.om

import fr.profi.pwx.util.mongodb.IMongoDbEntity
import reactivemongo.bson.BSONObjectID

case class MSAngelServerConfig(

  var id: Option[BSONObjectID] = None,

  var msconvertPath: Option[String] = None,
  var msDataConverterPath: Option[String] = None,
  var extractmsnPath: Option[String] = None,
  var raw2mzdbPath: Option[String] = None,
  var mzdbaccessPath: Option[String] = None,
  var mzdbaccessJavaArgs: Option[Array[String]] = None,

  var mascotUrl: Option[String] = None,

  var rawFilesMountPoint: Option[String] = None,
  var mzdbFilesMountPoint: Option[String] = None,
  var mascotMountPoint: Option[String] = None,
  var omssaMountPoint: Option[String] = None

) extends IMongoDbEntity {

  require(msconvertPath != null, "msconvertPath must not be null")
  require(msDataConverterPath != null, "msDataConverterPath must not be null")
  require(extractmsnPath != null, "extractmsnPath must not be null")
  require(raw2mzdbPath != null, "raw2mzdbPath must not be null")
  require(mzdbaccessPath != null, "mzdbaccessPath must not be null")

  require(mascotUrl!= null, "Mascot URL must not be null")
  
  require(mascotMountPoint != null, "mascotMountPoint must not be null")
  require(omssaMountPoint != null, "omssaMountPoint must not be null")
}

object MountPointsMapKeys {

  val RAW_FILES = "RAW_FILES"
  val PUTATIVE_RAW_FILES = "Putative_RAW_FILES"

  val MZDB_FILES = "MZDB_FILES"
  val PUTATIVE_MZDB_FILES = "Putative_MZDB_FILES"

  val MASCOT = SearchEngine.MASCOT.toString()
  val PUTATIVE_MASCOT = "Putative_" + SearchEngine.MASCOT

  val OMSSA = SearchEngine.OMSSA.toString()
  val PUTATIVE_OMSSA = "Putative_" + SearchEngine.OMSSA

  val ALL = "All"
}