package fr.profi.msangel.om.workflow

import scala.collection.mutable.HashMap

import org.joda.time.DateTime

import fr.profi.msangel.om.DataFileExtension
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
  var number: Option[Int] = None,
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

  /* Requirements */
  require(status != null, "Job status must not be null.")
  require(workflowTaskId matches "^[0-9a-f]+$", "invalid workflowTaskId")
  require(workflowTaskName != null && workflowTaskName.isEmpty() == false, "workflowTaskName must not be null nor empty.")
  require(inputFile != null && inputFile.isEmpty() == false, "Input file path must not be null nor empty.")
  
  /* Key for input file extension in execution variables */
  val INPUT_EXTENSION_KEY = "INPUT_EXTENSION_KEY"
  

  /** Return true if the job is complete */
  def isComplete(): Boolean = (status == WorkflowJobStatus.SUCCEEDED || status == WorkflowJobStatus.FAILED) //TODO || status == MsiSearchStatus.KILLED)
  //  def notYetPending(): Boolean = (status == WorkflowStatus.CREATED || status == WorkflowStatus.UPLOADING)

  /** Get the entry in job execution variables corresponding to the given file extension **/
  def getExecutionVariableForFormat(keyAsFormat: DataFileExtension.Value): String = {
    this.executionVariables(ExecutionVariable.getFormatKeyAsString(keyAsFormat))
  }

  /** Set the value in job execution variables corresponding to the given file extension **/
  def setExecutionVariableForFormat(keyAsFormat: DataFileExtension.Value, value: String): Unit = {
    this.executionVariables(ExecutionVariable.getFormatKeyAsString(keyAsFormat)) = value
  }

  /** Add formatted text to the job monitoringTrace field **/
  def addToMonitoringTrace(trace: String): Unit = {
    val currentTrace = this.monitoringTrace.getOrElse("") //shouldn't be None
    val updatedTrace = currentTrace + s"""\n[${DateTime.now().toString("dd/MM/yy, HH:mm:ss")}] - $trace"""
    this.monitoringTrace = Some(updatedTrace)
  }
}