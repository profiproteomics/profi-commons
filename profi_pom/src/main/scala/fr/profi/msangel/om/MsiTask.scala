package fr.profi.msangel.om

import org.joda.time.DateTime

/**
 * Model for Mass Spectrometry Identification task.
 */
case class MsiTask(

  /** Parameters */
  //var id: Option[String] = None, //TODO: extend MongoDbEntity

  var status: MsiTaskStatus.Value = MsiTaskStatus.CREATED,
  var searchIds: Option[Array[String]] = None, //mongo ids
  var progression: Int = 0, // nb of searches completed 
  var startTime: Option[DateTime] = None, //TODO: make sure it's not over when task is submitted.
  var stopTime: Option[DateTime] = None,

  val name: String,
  val inputFiles: Array[String],
  val searchForm: MsiSearchForm,
  val projectId: String,
  val ownerId: String,
  val scheduleType: String,
  val mergeMsMs: Boolean = false) {
  //extends IMsiObject {


  /** Requirements */

  require(searchIds != null, "searchIds array is required (None/Some)")
  if (searchIds.isDefined) {
    require(searchIds.get.distinct.length == searchIds.get.length, "searchIds must not contain duplicates.")
    require(searchIds.get.length == inputFiles.length, "searchIds and related inputFiles arrays are not of same length.")
    require(searchIds.get.forall(_ matches "^[0-9a-f]+$"), s"invalid mongodb object id: it must contain only hexadecimal characters")
  }

  require(status != null, "Task status must not be null.")
  require(name != null && !name.isEmpty(), "name must not be null nor empty")
  require(inputFiles != null && inputFiles.isEmpty == false, "Data file list must not be null nor empty.")
  require(inputFiles.distinct.length == inputFiles.length, "Data file list must not contain duplicates.") //TODO: make this requirement in UI
  require(searchForm != null, "searchForm must not be null")
  //TODO: finish/ adapt

  /** Utilities */

  def isComplete: Boolean = {
    (status == MsiTaskStatus.SUCCEEDED || status == MsiTaskStatus.FAILED || status == MsiTaskStatus.DELETED)
  }

  /** Some utility */
  def isFake: Boolean = searchForm.targetURL matches """.*fake.*"""

}

