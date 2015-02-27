package fr.profi.msangel.om.workflow

import org.joda.time.DateTime
import operation.IWorkflowOperation

case class Workflow(
  //  val properties: HashMap[String, String],
  val operations: Array[IWorkflowOperation],
  val isTemplate: Boolean = false,

  /** For templates only */
  val name: Option[String] = None,
  val ownerMongoId: Option[String] = None,
  val creationDate: Option[DateTime] = None) {
  //  require(properties != null, "Workflow properties must be defined")

  //  override def toString(): String = {
  //    "Workflow :" +
  //      "\nops : " + scala.runtime.ScalaRunTime.stringOf(operations) +
  //      "\nrest : " + (Seq(isTemplate, name, ownerMongoId, registTime).mkString(" - "))
  //  }
}