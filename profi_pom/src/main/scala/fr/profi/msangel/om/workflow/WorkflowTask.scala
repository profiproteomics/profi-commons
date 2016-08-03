package fr.profi.msangel.om.workflow

import org.joda.time.DateTime

import fr.profi.msangel.om.SchedulingType
import fr.profi.msangel.om.TaskStatus
import fr.profi.pwx.util.mongodb.IMongoDbEntity

import operation.IWorkflowOperation
import operation.PeaklistIdentification
import reactivemongo.bson.BSONObjectID

/**
 * Model for a worklfow task.
 */
case class WorkflowTask(

  /**
   *  Parameters
   */
  var id: Option[BSONObjectID] = None,
  var number: Option[Int] = None,
  var inputFiles: Array[String] = Array(),
  var workflowJobIds : Array[String] = Array(),
  var msiTaskIds : Array[String] = Array(),

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
  
  val isFake : Boolean = false
) extends IMongoDbEntity {

//  override def toString() = s"""
//WorkflowTask(
//  $id,
//  ${scala.runtime.ScalaRunTime.stringOf(inputFiles)}, 
//  ${scala.runtime.ScalaRunTime.stringOf(workflowJobIds)}, 
//  ${scala.runtime.ScalaRunTime.stringOf(msiTasksIds)},
//  $status,
//  $progression,
//  $creationDate,
//  $startDate,
//  $stopDate,
//  $fileMonitoringConfig,
//  Workflow(
//    None,
//    Array(),
//    ${workflow.isTemplate},
//    ${workflow.name},
//    ${workflow.ownerMongoId},
//    ${workflow.creationDate}
//  ),
//  $name,
//  $scheduleType,
//  $ownerMongoId,
//  $projectId,
//  $isFake
//)
//
//"""
  
  
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
    /*val isStatusOfTypeComplete = status ==  TaskStatus.SUCCEEDED || status == TaskStatus.FAILED || status == TaskStatus.KILLED

    // If task is in RTM mode, it may not be finished though its status is SUCCEEDED or FAILED
    if (this.isInRealTimeMonitoringMode == false) isStatusOfTypeComplete
    else {
      if (this.areRtmEndingConditionsReached() == false) false
      else isStatusOfTypeComplete
    }*/
    status == TaskStatus.SUCCEEDED || status == TaskStatus.FAILED || status == TaskStatus.KILLED
  }
  
  //def isPaused : Boolean = status == TaskStatus.PAUSED
  
  /** Get workflow operation at given index */
  def getOperation( index: Int): IWorkflowOperation = {
    this.workflow.operations(index)
  }

  /** Compute if selected workflow operation is of type PeaklistIedntification */
  def isOperationOfTypePeaklistIdentification(operation: IWorkflowOperation): Boolean = {
    operation match {
      case pi: PeaklistIdentification => true
      case _                          => false
    }
  }
  
  def isOperationOfTypePeaklistIdentification(operationIndex: Int): Boolean = {
    isOperationOfTypePeaklistIdentification( getOperation(operationIndex) )
  }
  
  def somePeaklistIdentification(): Boolean = {
    this.workflow.operations.foreach ( operation =>
      if (isOperationOfTypePeaklistIdentification(operation)) return true
    )
    false
  }
  
  def isInRealTimeMonitoringMode: Boolean = scheduleType == SchedulingType.REAL_TIME_MONITORING

  /** If scheduling type is real-time monitoring, compute if ending conditions are reached */
  def areRtmEndingConditionsReached(): Boolean = {

    require(isInRealTimeMonitoringMode, "Workflow task's scheduling mode is not of type 'Real-time monitoring'.")
    require(fileMonitoringConfig.isDefined, "File monitoring configuration is not defined.")

    val config = fileMonitoringConfig.get

    // Return true if ANY OF ending conditions is reached

    // Max date is over
    if (
      config.maxDate.isDefined
      && DateTime.now().isAfter(config.maxDate.get)
    ) return true

    // Max file count is reached
    import fr.profi.util.scala.dateTimeOrdering
    if (
      config.maxFileCount.isDefined
      && inputFiles.length >= config.maxFileCount.get //FIXME: as many registered as needed. Should be as many complete !
    ) return true
    
    //Max interval between acquisitions overheaded
    //TODO
    
    false
  }
  
}