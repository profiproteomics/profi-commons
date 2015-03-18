package fr.profi.msangel.om.workflow

import org.joda.time.DateTime

import fr.profi.pwx.util.mongodb.IMongoDbEntity

import operation.IWorkflowOperation
import reactivemongo.bson.BSONObjectID

case class Workflow(
  var id: Option[BSONObjectID] = None,
  //  val properties: HashMap[String, String],
  val operations: Array[IWorkflowOperation],
  val isTemplate: Boolean = false,

  /** For templates only */
  val name: Option[String] = None,
  val ownerMongoId: Option[String] = None,
  val creationDate: Option[DateTime] = None
) extends IMongoDbEntity {
  //  require(properties != null, "Workflow properties must be defined")

  //  override def toString(): String = {
  //    "Workflow :" +
  //      "\nops : " + scala.runtime.ScalaRunTime.stringOf(operations) +
  //      "\nrest : " + (Seq(isTemplate, name, ownerMongoId, registTime).mkString(" - "))
  //  }
}