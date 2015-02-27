package fr.profi.msangel.om.workflow

import org.joda.time.DateTime
import fr.profi.msangel.om.WorkflowJobStatus
import scala.collection.mutable.HashMap
import play.api.libs.json.JsObject
import play.api.libs.json.Json

/**
 * Model for Workflow job: execution of the workflow on one file.
 */
case class WorkflowJob(

  /** Parameters */

  var status: WorkflowJobStatus.Value = WorkflowJobStatus.CREATED,
  var startDate: Option[DateTime] = None,
  var stopDate: Option[DateTime] = None,
  var creationDate: Option[DateTime] = None,
  var progression: Int = 0, // % = #operations done / #operations in workflow
  var monitoringCallback: Option[String] = None,

  val name: String, //remove me?
  val workflowTaskId: String, //mongo id
  val workflowTaskName: String, //TODO ? delete me
  val inputFile: String,
  val executionVariables: HashMap[String, String] = HashMap()
) {

  /** Requirements */
  
  require(status != null, "Job status must not be null.")
  require(workflowTaskId matches "^[0-9a-f]+$", "invalid workflowTaskId")
  require(workflowTaskName != null && workflowTaskName.isEmpty() == false, "workflowTaskName must not be null nor empty.")
  require(inputFile != null && inputFile.isEmpty() == false, "Input file path must not be null nor empty.")

  /** Utilities */
  
  def isComplete(): Boolean = (status == WorkflowJobStatus.SUCCEEDED || status == WorkflowJobStatus.FAILED) //TODO || status == MsiSearchStatus.KILLED)

  //  def notYetPending(): Boolean = (status == WorkflowStatus.CREATED || status == WorkflowStatus.UPLOADING)
}