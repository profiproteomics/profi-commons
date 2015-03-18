package fr.profi.msangel

import scala.util.Try
import org.joda.time.DateTime
import play.api.libs.functional.syntax._
import play.api.libs.json._
import fr.profi.msangel.om.msi._
import fr.profi.msangel.om.workflow._
import fr.profi.msangel.om.workflow.operation._
import fr.profi.pwx.util.json.PWXJson
import reactivemongo.bson.BSONObjectID
import reactivemongo.bson.BSONValue

package object om {

  /** 
   *  Implicit object handling the serialization/deserialization of BSONObjectID
   **/
  implicit object BSONObjectIDFormat extends Format[BSONObjectID] {

    def writes(objectId: BSONObjectID): JsValue = {
      Json.obj("$oid" -> objectId.stringify)
    }

    def reads(json: JsValue): JsResult[BSONObjectID] = {
      val oidOpt = (json \ "$oid").asOpt[String]

      if (oidOpt.isDefined) JsSuccess(BSONObjectID(oidOpt.get))
      else JsSuccess(null)
    }
  }

  /**
   *  Automatically parse Enumeration values from/to Json.
   */
  trait JsonEnumeration extends Enumeration {
    implicit val enumReads: Reads[this.Value] = EnumUtils.enumReads(this)
    implicit def enumWrites: Writes[this.Value] = EnumUtils.enumWrites

    /** Compute if enumeration contains given key */
    //TODO : at Enumeration level
    def contains(test: String): Boolean = {
      try {
        this.withName(test)
        true
      } catch {
        case t: Throwable => false
      }
    }

    def contains(key: this.Value): Boolean = {
      this.values.contains(key)
    }
  }

  /**
   * Support to convert enumerations to Json.
   * See: http://stackoverflow.com/questions/15488639/how-to-write-readst-and-writest-in-scala-enumeration-play-framework-2-1/15489179#15489179
   */
  //TODO: get it from fr.profi dependency ?
  object EnumUtils {

    def enumReads[E <: Enumeration](enum: E): Reads[E#Value] = new Reads[E#Value] {
      def reads(json: JsValue): JsResult[E#Value] = json match {
        case JsString(s) => {
          try {
            JsSuccess(enum.withName(s))
          } catch {
            case _: NoSuchElementException => JsError(s"Enumeration expected of type: '${enum.getClass}', but it does not appear to contain the value: '$s'")
          }
        }
        case _ => JsError("String value expected")
      }
    }

    implicit def enumWrites[E <: Enumeration]: Writes[E#Value] = new Writes[E#Value] {
      def writes(v: E#Value): JsValue = JsString(v.toString)
    }

    implicit def enumFormat[E <: Enumeration](enum: E): Format[E#Value] = {
      Format(enumReads(enum), enumWrites)
    }
  }

  /**
   * Sort joda.DateTime
   */
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

  /**
   * Some Play!2.2 JSON formatters for MS-Angel case classes.
   */
  implicit val msangelServerConfigFormat = Json.format[MSAngelServerConfig]

  implicit val msiSearchFormFormat = PWXJson.entityFormat(Json.format[MsiSearchForm])
  implicit val msiSearchFormat = PWXJson.entityFormat(Json.format[MsiSearch])

  implicit val workflowFormat = PWXJson.entityFormat(Json.format[Workflow])
  implicit val workflowJobFormat = PWXJson.entityFormat(Json.format[WorkflowJob])

  implicit val fileMonitoringConfigFormat = Json.format[FileMonitoringConfig]

  implicit val workflowTaskFormat = PWXJson.entityFormat(Json.format[WorkflowTask])
  implicit val msiTaskFormat = PWXJson.entityFormat(Json.format[MsiTask])
}