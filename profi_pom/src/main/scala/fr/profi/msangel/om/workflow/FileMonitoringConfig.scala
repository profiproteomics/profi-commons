package fr.profi.msangel.om.workflow

import java.util.Date
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer
import java.io.File

case class FileMonitoringConfig(
  val directory: String,
  val wildcardPattern: String = "", // may contain wildcards represented by asterisks and question marks

  val newFilesOnly: Boolean = false,
  val includeSubDirs: Boolean = false,

  val maxFileCount: Option[Int] = None,
  val minDate: Option[DateTime] = None,
  val maxDate: Option[DateTime] = None, //because LocalDate has no implicit json format //FIXME
  val maxIntervalBetweenAcquisition: Option[DateTime] = None  
) {

  require(directory != null && directory != "") //adapt when wild cards are handled

  //TODO: move to PWX COMMONS !!!
  // Source: http://www.rgagnon.com/javadetails/java-0515.html
//  def wildcardToRegex(wildcard: String): String = {
  /*def getFileNameFilterAsRegex(): String = {
    val wildcardLength = wildcardPattern.length()

    val sb = new StringBuilder(wildcardLength)

    sb.append('^')

    for (c <- wildcardPattern.toCharArray()) {
      c match {
        case '*' => {
          sb.append(".*")
        }
        case '?' => {
          sb.append('.')
        }
        // escape special regexp-characters
        //case '(': case ')': case '[': case ']': case '$':
        //case '^': case '.': case '{': case '}': case '|':
        case '\\' => {
          sb.append("\\")
          sb.append(c)
        }
        case _ =>
          sb.append(c)
      }
    }

    sb.append('$')

    sb.result()
  }*/

}