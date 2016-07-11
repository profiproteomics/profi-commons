package fr.profi.msangel.om.msi

import org.joda.time.DateTime

import fr.profi.msangel.om.TaskStatus
import fr.profi.pwx.util.mongodb.IMongoDbEntity

import reactivemongo.bson.BSONObjectID

/**
 * Model for Mass Spectrometry Identification task.
 */

case class MsiTask(

  /**
   *  Parameters
   */
  var id: Option[BSONObjectID] = None,
  var number: Option[Int] = None,
  var inputFiles: Array[String] = Array(),
  var searchIds: Array[String] = Array(), //mongo ids

  var status: TaskStatus.Value = TaskStatus.CREATED, //MsiTaskStatus.CREATED
  var progression: Int = 0, // nb of searches completed 
  //var percentComplete: Int = 0,
  var startDate: Option[DateTime] = None, //TODO: make sure it's not over when task is submitted.
  var stopDate: Option[DateTime] = None,
  var creationDate: Option[DateTime] = None,

  val isFake: Boolean = false,
  val name: String,
  val searchForm: MsiSearchForm, //not only ID: keep trace of used params if template changes later (+ convenient for graphical purposes)
  val ownerMongoId: String, //mongo ID
  val workflowTaskId: String

  //val mergeMsMs: Boolean = false, //TODO : delete me? (file conversion option)
  //val startAtDate: Option[java.util.Date] = None
  
) extends IMongoDbEntity {
  //extends IMsiObject {

  /**
   *  Utilities
   */
  def isComplete: Boolean = (status == TaskStatus.SUCCEEDED || status == TaskStatus.FAILED || status == TaskStatus.KILLED)
  //def isFake: Boolean = searchForm.targetURL matches """.*fake.*"""

  /**
   * Requirements
   */

  require(searchIds != null, "searchIds array is required (None/Some)")
  require(searchIds.distinct.length == searchIds.length, "searchIds must not contain duplicates.")
  require(searchIds.length == inputFiles.length, "searchIds and related inputFiles arrays are not of same length.")
  require(searchIds.forall(_ matches "^[0-9a-f]+$"), s"invalid mongodb object id: it must contain only hexadecimal characters")

  require(status != null, "Task status must not be null.")
  require(name != null && !name.isEmpty(), "name must not be null nor empty")
  //require(inputFiles != null && inputFiles.isEmpty == false, "Data file list must not be null nor empty.")
  //require(inputFiles.distinct.length == inputFiles.length, "Data file list must not contain duplicates.") //TODO: make this requirement in UI
  require(searchForm != null, "searchForm must not be null")

  require(ownerMongoId != null, "Task's owner mongo ID must not be null") //TODO : hexaDec + size

  //  if (projectId.isDefined) {
  //    require(projectId.get > 0, "Invalid projectId")
  //    require(instrumentConfigId.get > 0, "Invalid instrumentConfigId")
  //    require(peaklistSoftwareId.get > 0, "Invalid peaklistSoftwareId")
  //  }
}
