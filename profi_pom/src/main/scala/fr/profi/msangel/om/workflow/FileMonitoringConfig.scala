package fr.profi.msangel.om.workflow

import java.util.Date
import org.joda.time.DateTime

case class FileMonitoringConfig(
  val folderPath: String,
  val regex: String,

  val newFilesOnly: Boolean = false,
  val includeSubFolders: Boolean = false,

  val maxFileCount: Option[Int],
  val maxDate: Option[DateTime], //because LocalDate has no implicit json format //FIXME
  val maxIntervalBetweenAcquisition: Option[DateTime]
) {

  require(folderPath != null && folderPath != "") //adapt when wild cards are handled
}

