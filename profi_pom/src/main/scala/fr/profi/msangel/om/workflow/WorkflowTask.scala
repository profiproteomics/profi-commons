package fr.profi.msangel.om.workflow

import org.joda.time.DateTime

import fr.profi.msangel.om.SchedulingType
import fr.profi.msangel.om.TaskStatus

import operation.IWorkflowOperation
import operation.PeaklistIdentification

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
  var creationDate: Option[DateTime] = None,
  var startDate: Option[DateTime] = None, //TODO: make sure it's not over when task is submitted.
  var stopDate: Option[DateTime] = None,

  val fileMonitoringConfig: Option[FileMonitoringConfig] = None,
  val workflow: Workflow,
  val name: String,
  val scheduleType: SchedulingType.Value,
  val ownerMongoId: String, //mongo ID
  val projectId : Option[Long] = None, //uds ID
  
  val isFake : Boolean
  ) {

  /**
   * Requirements
   */
  require(status != null, "Task status must not be null.")
  require(status !=  TaskStatus.UPLOADING, "Uploading is not an appropriate status for workflow task")
  //TODO : paused, deleted?
  
  require(workflow != null, "workflow must not be null") //TODO : hexaDec + size
  require(name != null && !name.isEmpty(), "name must not be null nor empty")
  require(ownerMongoId != null, "Task's owner mongo ID must not be null") //TODO : hexaDec + size
  
  /**
   *  Utilities
   */
  def isComplete: Boolean = {
    val successOrFail = status ==  TaskStatus.SUCCEEDED || status == TaskStatus.FAILED //TODO || status == WorkflowTaskStatus.DELETED)

    // If task is in RTM mode, it may not be finished though its status is SUCCEEDED or FAILED
    if (this.isInRealTimeMonitoringMode()) {
      if (this.areRtmEndingConditionsReached() == false) false
      else successOrFail

    } else {
      successOrFail
    }
  }
  
  /** Get workflow operation at given index */
  def getOperation( index: Int): IWorkflowOperation = {
    this.workflow.operations(index)
  }

  /** Compute if selected workflow operation is of type PeaklistIedntification */
  def isOpeartionOfTypePeaklistIdentification(operation: IWorkflowOperation): Boolean = {
    operation match {
      case pi: PeaklistIdentification => true
      case _                          => false
    }
  }
  
  def isOperationOfTypePeaklistIdentification(operationIndex: Int): Boolean = {
    val operation = this.getOperation(operationIndex)
    this.isOpeartionOfTypePeaklistIdentification(operation)
  }
  
  def isInRealTimeMonitoringMode(): Boolean = scheduleType == SchedulingType.REAL_TIME_MONITORING

  /** If scheduling type is real-time monitoring, compute if ending conditions are reached */
  def areRtmEndingConditionsReached(): Boolean = {
    require(isInRealTimeMonitoringMode(), "Workflow task's scheduling mode is not of type 'Real-time monitoring'.")
    require(fileMonitoringConfig.isDefined, "File monitoring configuration is not defined.")

    val config = fileMonitoringConfig.get

    // Return true if ANY OF ending conditions is reached
    import fr.profi.msangel.om.dateTimeOrdering

    // Max date has passed
    if (config.maxDate.isDefined
      && DateTime.now().isAfter(config.maxDate.get)
    ) return true

    // Max file count is reached
    if (
      config.maxFileCount.isDefined
      && inputFiles.length >= config.maxFileCount.get
    ) return true
    
    //Max interval between acquisitions overheaded
    //TODO
    
    false
  }
  
}