package fr.profi.msangel.om.workflow

import fr.profi.msangel.om.WorkflowStatus
import org.joda.time.DateTime

/**
 * Model for Workflow job: execution of the workflow on one file.
 */
case class WorkflowJob(

  /** Parameters */

  var status: WorkflowStatus.Value = WorkflowStatus.CREATED,
  var startTime: Option[DateTime] = None,
  var stopTime: Option[DateTime] = None,
  var progression: Int = 0, //number of operations completed
  var monitoringCallback: Option[String] = None,

  val name: String,
  val workflowTaskId: String, //mongo id
  val workflowTaskName: String, //TODO ? delete me
  val inputFile: String) {

  /** Requirements */
  
  require(status != null, "Job status must not be null.")
  require(workflowTaskId matches "^[0-9a-f]+$", "invalid workflowTaskId")
  require(workflowTaskName != null && workflowTaskName.isEmpty() == false, "workflowTaskName must not be null nor empty.")
  require(inputFile != null && inputFile.isEmpty() == false, "Input file path must not be null nor empty.")

  /** Utilities */
  
  def isComplete(): Boolean = (status == WorkflowStatus.SUCCEEDED || status == WorkflowStatus.FAILED) //TODO || status == MsiSearchStatus.KILLED)

  //  def notYetPending(): Boolean = (status == WorkflowStatus.CREATED || status == WorkflowStatus.UPLOADING)
}