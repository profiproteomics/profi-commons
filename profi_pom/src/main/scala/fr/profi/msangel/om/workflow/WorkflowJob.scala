package fr.profi.msangel.om.workflow

import scala.collection.mutable.HashMap

import org.joda.time.DateTime

import fr.profi.msangel.om.DataFileFormat
import fr.profi.msangel.om.ExecutionVariable
import fr.profi.msangel.om.WorkflowJobStatus
import fr.profi.pwx.util.mongodb.IMongoDbEntity

import reactivemongo.bson.BSONObjectID

/**
 * Model for Workflow job: execution of the workflow on one file.
 */
case class WorkflowJob(

  /** Parameters */
  var id: Option[BSONObjectID] = None,
  var status: WorkflowJobStatus.Value = WorkflowJobStatus.CREATED,
  var startDate: Option[DateTime] = None,
  var stopDate: Option[DateTime] = None,
  var creationDate: Option[DateTime] = None,
  var progression: Int = 0, // % = #operations done / #operations in workflow
  var monitoringTrace: Option[String] = None,

  val name: String, //remove me?
  val workflowTaskName: String, //TODO ? delete me
  val workflowTaskId: String, //mongo id
  val msiSearchIdBySearchEngine: HashMap[String, String] = HashMap(), //searchEngine -> msiSearchId
  val inputFile: String,
  val executionVariables: HashMap[String, String] = HashMap()
) extends IMongoDbEntity {

  /** Requirements */
  
  require(status != null, "Job status must not be null.")
  require(workflowTaskId matches "^[0-9a-f]+$", "invalid workflowTaskId")
  require(workflowTaskName != null && workflowTaskName.isEmpty() == false, "workflowTaskName must not be null nor empty.")
  require(inputFile != null && inputFile.isEmpty() == false, "Input file path must not be null nor empty.")

  /** Utilities */
  def isComplete(): Boolean = (status == WorkflowJobStatus.SUCCEEDED || status == WorkflowJobStatus.FAILED) //TODO || status == MsiSearchStatus.KILLED)
  //  def notYetPending(): Boolean = (status == WorkflowStatus.CREATED || status == WorkflowStatus.UPLOADING)

  def getExecutionVariableForFormat(keyAsFormat: DataFileFormat.Value): String = {
    this.executionVariables(ExecutionVariable.getFormatKeyAsString(keyAsFormat))
  }
  def setExecutionVariableForFormat(keyAsFormat: DataFileFormat.Value, value: String): Unit = {
    this.executionVariables(ExecutionVariable.getFormatKeyAsString(keyAsFormat)) = value
  }

  def addToMonitoringTrace(trace: String): Unit = {
    val currentTrace = this.monitoringTrace.getOrElse("") //shouldn't be None
    val updatedTrace = currentTrace + s"""\n[${DateTime.now().toString("dd/MM/yy, HH:mm:ss")}] - $trace"""
    this.monitoringTrace = Some(updatedTrace)
  }
}