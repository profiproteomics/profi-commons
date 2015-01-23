package fr.profi.msangel.om

import play.api.libs.functional.syntax._
import play.api.libs.json._
import fr.profi.msangel.om.workflow._

package object implicits {

  /**
   *  Automatically parse Enumeration values from/to Json.
   */
  trait JsonEnumeration extends Enumeration {
    implicit val enumReads: Reads[this.Value] = EnumUtils.enumReads(this)
    implicit def enumWrites: Writes[this.Value] = EnumUtils.enumWrites
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
   * Some Play!2.2 JSON formatters for MS-Angel case classes.
   */
  implicit val fileMonitoringConfigFormat = Json.format[FileMonitoringConfig]
  implicit val msangelServerConfigFormat = Json.format[MSAngelServerConfig]
  implicit val workflowFormat = Json.format[Workflow]
  
  implicit val msiSearchFormFormat = Json.format[MsiSearchForm]
  implicit val msiSearchFormat = Json.format[MsiSearch]
  implicit val msiTaskFormat = Json.format[MsiTask]

}