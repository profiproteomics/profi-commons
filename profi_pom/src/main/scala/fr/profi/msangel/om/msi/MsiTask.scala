package fr.profi.msangel.om

import org.joda.time.DateTime
import fr.profi.msangel.om.workflow.FileMonitoringConfig

/**
 * Model for Mass Spectrometry Identification task.
 */

case class MsiTask(

  /**
   *  Parameters
   */
  //var id: Option[String] = None, //TODO: extend MongoDbEntity

  var inputFiles: Array[String] = Array(),
  var searchIds: Array[String] = Array(), //mongo ids

  var status: TaskStatus.Value = TaskStatus.CREATED, //MsiTaskStatus.CREATED
  var progression: Int = 0, // nb of searches completed 
  //var percentComplete: Int = 0,
  var startTime: Option[DateTime] = None, //TODO: make sure it's not over when task is submitted.
  var stopTime: Option[DateTime] = None,

  val name: String,
  val searchForm: MsiSearchForm, // TODO : searchForm ID ? (doesn't fit display needs that much)
  val ownerMongoId: String //mongo ID
  
  //val mergeMsMs: Boolean = false, //TODO : delete me? (file conversion option)
  //val workflowId: String, // MongoId 
  //val scheduleType: SchedulingType.Value, //TODO : DELETE ME
  //val fileMonitoringConfig: Option[FileMonitoringConfig] = None, //TODO : DELETE ME
  //val startAtDate: Option[java.util.Date] = None,
  //val projectId: Option[Long], //uds ID //TODO : DELETE ME
  //val instrumentConfigId: Option[Long], //uds ID //TODO : DELETE ME
  //val peaklistSoftwareId: Option[Long] //uds ID //TODO : DELETE ME
  ) {
  //extends IMsiObject {

  /**
   *  Utilities
   */
  def isComplete: Boolean = (status == TaskStatus.SUCCEEDED || status == TaskStatus.FAILED || status == TaskStatus.DELETED)
  def isFake: Boolean = searchForm.targetURL matches """.*fake.*"""

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

// BEFORE REFACTORING

//case class MsiTask(
//
//  /**
//   *  Parameters
//   */
//  //var id: Option[String] = None, //TODO: extend MongoDbEntity
//
//  var status: MsiTaskStatus.Value = MsiTaskStatus.CREATED,
//  var searchIds: Option[Array[String]] = None, //mongo ids
//  var progression: Int = 0, // nb of searches completed 
//  //  var percentComplete: Int = 0,
//  var startTime: Option[DateTime] = None, //TODO: make sure it's not over when task is submitted.
//  var stopTime: Option[DateTime] = None,
//
//  val name: String,
//
//  val inputFiles: Array[String],
//  val searchForm: MsiSearchForm,
//  val mergeMsMs: Boolean = false,
//
//  val scheduleType: SchedulingType.Value,
//  val fileMonitoringConfig: Option[FileMonitoringConfig] = None,
//  //val startAtDate: Option[java.util.Date] = None,
//  var workflowId: Option[String] = None, // MongoId
//
//  val ownerMongoId: String, //mongo ID
//  val projectId: Option[Long], //uds ID
//  val instrumentConfigId: Option[Long], //uds ID
//  val peaklistSoftwareId: Option[Long] //uds ID
//  ) {
//  //extends IMsiObject {
//
//  /**
//   *  Utilities
//   */
//  def isComplete: Boolean = (status == MsiTaskStatus.SUCCEEDED || status == MsiTaskStatus.FAILED || status == MsiTaskStatus.DELETED)
//  def isFake: Boolean = searchForm.targetURL matches """.*fake.*"""
//
//  /**
//   * Requirements
//   */
//
//  require(searchIds != null, "searchIds array is required (None/Some)")
//  if (searchIds.isDefined) {
//    require(searchIds.get.distinct.length == searchIds.get.length, "searchIds must not contain duplicates.")
//    require(searchIds.get.length == inputFiles.length, "searchIds and related inputFiles arrays are not of same length.")
//    require(searchIds.get.forall(_ matches "^[0-9a-f]+$"), s"invalid mongodb object id: it must contain only hexadecimal characters")
//  }
//
//  require(status != null, "Task status must not be null.")
//  require(name != null && !name.isEmpty(), "name must not be null nor empty")
//  require(inputFiles != null && inputFiles.isEmpty == false, "Data file list must not be null nor empty.")
//  require(inputFiles.distinct.length == inputFiles.length, "Data file list must not contain duplicates.") //TODO: make this requirement in UI
//  require(searchForm != null, "searchForm must not be null")
//
//  require(ownerMongoId != null, "Task's owner mongo ID must not be null") //TODO : hexaDec + size
//  if (projectId.isDefined) {
//    require(projectId.get > 0, "Invalid projectId")
//    require(instrumentConfigId.get > 0, "Invalid instrumentConfigId")
//    require(peaklistSoftwareId.get > 0, "Invalid peaklistSoftwareId")
//  }
//}

// BEFORE REFACTORING
