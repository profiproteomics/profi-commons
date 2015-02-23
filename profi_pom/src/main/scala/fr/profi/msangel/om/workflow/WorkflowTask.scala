package fr.profi.msangel.om.workflow

import fr.profi.msangel.om.SchedulingType
import org.joda.time.DateTime
import fr.profi.msangel.om.TaskStatus

/**
 *
 * Model for a worklfow task.
 */
case class WorkflowTask(

  /**
   *  Parameters
   */
  var inputFiles: Array[String] = Array(),
  var workflowJobIds : Array[String] = Array(),
  var msiTasksIds : Array[String] = Array(),

  var status: TaskStatus.Value = TaskStatus.CREATED,
  var progression: Int = 0, // nb of steps completed 
  var startTime: Option[DateTime] = None, //TODO: make sure it's not over when task is submitted.
  var stopTime: Option[DateTime] = None,

  val fileMonitoringConfig: Option[FileMonitoringConfig] = None,
  val workflow: Workflow,
  val name: String,
  val scheduleType: SchedulingType.Value,
  val ownerMongoId: String, //mongo ID
  val projectId : Option[Long] = None, //uds ID
  
  val isFake : Boolean
  ) {

  /**
   *  Utilities
   */
  def isComplete: Boolean = (status ==  TaskStatus.SUCCEEDED || status == TaskStatus.FAILED ) //TODO || status == WorkflowTaskStatus.DELETED)

  /**
   * Requirements
   */
  require(status != null, "Task status must not be null.")
  require(status !=  TaskStatus.UPLOADING, "Uploading is not an appropriate status for workflow task")
  //TODO : paused, deleted?
  
  require(workflow != null, "workflow must not be null") //TODO : hexaDec + size
  require(name != null && !name.isEmpty(), "name must not be null nor empty")
  require(ownerMongoId != null, "Task's owner mongo ID must not be null") //TODO : hexaDec + size
}