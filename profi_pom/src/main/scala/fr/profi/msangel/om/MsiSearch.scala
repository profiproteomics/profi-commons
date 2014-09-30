package fr.profi.msangel.om

import org.joda.time.DateTime
import scalafx.beans.property.ObjectProperty
//import scalafx.scene.control.Hyperlink
//import scalafx.Includes.handle
//import java.net.URI

/**
 * Model for Mass Spectrometry Identification search.
 */

// TODO: extends MongoDbEntity
case class MsiSearch( //one search <=> one input file

  /** Parameters */

  var status: MsiSearchStatus.Value = MsiSearchStatus.CREATED,
  var resultFile: Option[String] = None,
  var startTime: Option[DateTime] = None,
  var stopTime: Option[DateTime] = None,
  var percentComplete: Int = 0,
  var mascotId: Option[Long] = None, //TODO : rename into jobId => Mascot(/other) job number when found
  var serverResponseBody: Option[String] = None, //Mascot(/other) job number when found

  val name: String,
  val taskId: String, //mongo id
  val taskName: String,
  val inputFile: String
) {
  //extends IMsiObject {

  /** Requirements */

  require(status != null, "Search status must not be null.")
  require(percentComplete >= 0 && percentComplete <= 100, "Progression must be 0-100 %. ")
  require(taskId matches "^[0-9a-f]+$", "invalid taskId")
  require(taskName != null && taskName.isEmpty() == false, "taskName must not be null nor empty.")
  require(inputFile != null && inputFile.isEmpty() == false, "Input file path must not be null nor empty.")
  //allow task name to be empty. Default task name will be attributed on server side

  /** Utilities */
  def isComplete(): Boolean = (status == MsiSearchStatus.SUCCEEDED || status == MsiSearchStatus.FAILED || status == MsiSearchStatus.KILLED)

  def notYetPending(): Boolean = (status == MsiSearchStatus.CREATED || status == MsiSearchStatus.UPLOADING)
}


