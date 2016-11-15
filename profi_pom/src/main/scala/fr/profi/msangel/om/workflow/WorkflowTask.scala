package fr.profi.msangel.om.workflow

import org.joda.time.DateTime

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

import reactivemongo.bson.BSONObjectID

import fr.profi.msangel.om.SchedulingType
import fr.profi.msangel.om.TaskStatus
import fr.profi.msangel.om.workflow.operation.FileConversion
import fr.profi.msangel.om.workflow.operation.IWorkflowJobOperation
import fr.profi.msangel.om.workflow.operation.IWorkflowOperation
import fr.profi.msangel.om.workflow.operation.IWorkflowTaskOperation
import fr.profi.msangel.om.workflow.operation.MzdbRegistration
import fr.profi.msangel.om.workflow.operation.PeaklistIdentification
import fr.profi.pwx.util.mongodb.IMongoDbEntity

import operation.PeaklistIdentification


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

  var executionVariables: Option[HashMap[String, String]] = None,
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
  val projectId: Option[Long] = None, //uds ID

  val isFake: Boolean = false
) extends IMongoDbEntity {
  
  /* Requirements */
  require(status != null, "Task status must not be null.")
  require(status !=  TaskStatus.UPLOADING, "Uploading is not an appropriate status for workflow task")
  //TODO : paused, deleted?
  
  require(workflow != null, "workflow must not be null") //TODO : hexaDec + size
  require(name != null && !name.isEmpty(), "name must not be null nor empty")
  require(ownerMongoId != null, "Task's owner mongo ID must not be null") //TODO : hexaDec + size
  
  /** Compute if the task is complete, based on its status **/
  def isComplete: Boolean = status == TaskStatus.SUCCEEDED || status == TaskStatus.FAILED || status == TaskStatus.KILLED

  //def isPaused : Boolean = status == TaskStatus.PAUSED

  /** Get workflow operation at given index **/
  def getOperation(index: Int): IWorkflowOperation = {
    this.workflow.operations(index)
  }

  /** Compute if selected workflow operation is of type PeaklistIedntification **/
  def isOperationOfTypePeaklistIdentification(operation: IWorkflowOperation): Boolean = {
    operation match {
      case pi: PeaklistIdentification => true
      case _                          => false
    }
  }

  /** Get the 'task' operations of the workflow **/
  // TODO? Move to Workflow?
  def getTaskOperations(): Array[IWorkflowTaskOperation] = {
    val buffer = ArrayBuffer[IWorkflowTaskOperation]()
    for (op <- workflow.operations) {
      op match {
        case taskOp: IWorkflowTaskOperation => buffer += taskOp
        case _                              => {}
      }
    }
    buffer.result().toArray
  }

  /** Split operations depending on their type: 'job operations' and 'task operations' **/
  def splitJobAndTaskOperations(): (Array[IWorkflowJobOperation], Array[IWorkflowTaskOperation]) = {

    //workflow.operations.partition (_.isJobOperation)

    val jobOpsBuffer = ArrayBuffer[IWorkflowJobOperation]()
    val taskOpsBuffer = ArrayBuffer[IWorkflowTaskOperation]()

    for (op <- workflow.operations) {
      op match {
        case jobOp: IWorkflowJobOperation   => jobOpsBuffer += jobOp
        case taskOp: IWorkflowTaskOperation => taskOpsBuffer += taskOp
        case _                              => {}
      }
    }
    (jobOpsBuffer.result().toArray, taskOpsBuffer.result().toArray)
  }

  
  /** Compute if selected workflow operation is of type PeaklistIedntification **/
  def isOperationOfTypePeaklistIdentification(operationIndex: Int): Boolean = {
    isOperationOfTypePeaklistIdentification(getOperation(operationIndex))
  }

  /** Compute if there are 'task' operations in workflow **/
  def hasSomeTaskOperation(operations: Option[Array[IWorkflowOperation]] = None): Boolean = {
    operations.getOrElse(workflow.operations).find(_.isInstanceOf[IWorkflowTaskOperation]).isDefined
  }

  /** Compute if there is some file conversion in the workflow **/
  def hasSomeFileConversion(operations: Option[Array[IWorkflowOperation]] = None): Boolean = {
    operations.getOrElse(workflow.operations).find(_.isInstanceOf[FileConversion]).isDefined
  }

  /** Compute if there is some mzDB registration in the workflow **/
  def hasSomeMzdbRegistration(operations: Option[Array[IWorkflowOperation]] = None): Boolean = {
    operations.getOrElse(workflow.operations).find(_.isInstanceOf[MzdbRegistration]).isDefined
  }

  /** Compute if there is some peaklist identification in the workflow **/
  def hasSomePeaklistIdentification(operations: Option[Array[IWorkflowOperation]] = None): Boolean = {
    operations.getOrElse(workflow.operations).find(_.isInstanceOf[PeaklistIdentification]).isDefined
  }
  
  /*def hasSome(operationType: Class[_]): Boolean = {
    workflow.operations.find(_.isInstanceOf[operationType.getClass]).isDefined
  }*/
  
  /** Compute if the task scheduling mode is Real Time Monitoring **/
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
}