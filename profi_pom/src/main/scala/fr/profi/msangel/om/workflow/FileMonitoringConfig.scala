package fr.profi.msangel.om.workflow

import java.util.Date

case class FileMonitoringConfig(
  val folderPath: String,
  val regex: String,

  val newFilesOnly: Boolean = false,
  val includeSubFolders: Boolean = false,

  val maxFileCount: Option[Int],
  val maxDate: Option[Date], //because LocalDate has no implicit json format //FIXME
  val maxIntervalBetweenAcquisition: Option[Date]
) {

  require(folderPath != null && folderPath != "") //adapt when wild cards are handled
}

