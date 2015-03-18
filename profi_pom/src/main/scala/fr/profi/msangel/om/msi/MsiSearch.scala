package fr.profi.msangel.om.msi

import org.joda.time.DateTime

import fr.profi.msangel.om.MsiSearchStatus
import fr.profi.pwx.util.mongodb.IMongoDbEntity

import reactivemongo.bson.BSONObjectID

/**
 * Model for Mass Spectrometry Identification search.
 */

// TODO: extends MongoDbEntity
case class MsiSearch( //one search <=> one input file

  /** Parameters */
  var id: Option[BSONObjectID] = None,
  var status: MsiSearchStatus.Value = MsiSearchStatus.CREATED,
  var resultFile: Option[String] = None,
  var startDate: Option[DateTime] = None,
  var stopDate: Option[DateTime] = None,
  var creationDate: Option[DateTime] = None,
  var percentComplete: Int = 0,
  var mascotId: Option[Long] = None, //TODO : rename into jobId => Mascot(/other) job number when found
  //  var serverResponseBody: Option[String] = None,
  var submissionCallback: Option[String] = None,
  var monitoringCallback: Option[String] = None, 

  val name: String,
  val msiTaskId: String, //mongo id
  val msiTaskName: String,
  val workflowJobId: String,
  val inputFile: String
) extends IMongoDbEntity {
  //extends IMsiObject { serverResponse

  /** Requirements */
  require(status != null, "Search status must not be null.")
  require(percentComplete >= 0 && percentComplete <= 100, "Progression must be 0-100 %. ")
  require(workflowJobId matches "^[0-9a-f]+$", "invalid workflowJobId")
  require(msiTaskId matches "^[0-9a-f]+$", "invalid taskId")
  require(msiTaskName != null && msiTaskName.isEmpty() == false, "taskName must not be null nor empty.")
  require(inputFile != null && inputFile.isEmpty() == false, "Input file path must not be null nor empty.")
  //allow task name to be empty. Default task name will be attributed on server side

  /** Utilities */
  def isComplete(): Boolean = (status == MsiSearchStatus.SUCCEEDED || status == MsiSearchStatus.FAILED || status == MsiSearchStatus.KILLED)

  def notYetPending(): Boolean = (status == MsiSearchStatus.CREATED || status == MsiSearchStatus.UPLOADING)
}


