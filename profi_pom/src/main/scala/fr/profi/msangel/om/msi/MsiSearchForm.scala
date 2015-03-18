package fr.profi.msangel.om.msi

import org.joda.time.DateTime

import play.api.libs.json.JsObject
import play.api.libs.json.Json

import fr.profi.msangel.om.MascotSearchParam
import fr.profi.msangel.om.SearchEngine
import fr.profi.pwx.util.mongodb.IMongoDbEntity

import reactivemongo.bson.BSONObjectID

/**
 * Model for Mass Spectrometry Identification search form
 * (parameters requiered by search server).
 */
case class MsiSearchForm(

  /** Parameters */
  var id: Option[BSONObjectID] = None,
  val targetURL: String, //TODO: move to MsiTask? (replace by isFake)
  val searchEngine: SearchEngine.Value,
  val paramMap: JsObject,
  val isTemplate: Boolean = false,

  /** For templates only */
  val name: Option[String] = None,
  val ownerMongoId: Option[String] = None,
  val creationDate: Option[DateTime] = None
) extends IMongoDbEntity {
  //extends IMsiObject {

  /** Requirements */
  require(targetURL != null, "targetURL must not be null.")
  require(targetURL.isEmpty() == false, "targetURL must not be empty.")
  require(searchEngine != null, "Search engine must not be null")
  require(paramMap != null, "Some paramMap must be provided")

  /** Some class utilities */
  def get(key: MascotSearchParam.Value): String = ((paramMap \ key.toString()).asOpt[String]).getOrElse("")
  def getOpt(key: MascotSearchParam.Value): Option[String] = (paramMap \ key.toString()).asOpt[String]
}

object DefaultMascotSearchForm {
  def apply() = {
    MsiSearchForm(
      targetURL = "http://www.matrixscience.com/cgi",
      searchEngine = SearchEngine.MASCOT,
      paramMap = Json.obj(),
      isTemplate = true
    )
  }
}

// BEFORE REFACTORING


//case class MsiSearchForm(
//
//  /** Parameters */
//  val targetURL: String, //TODO: move to MsiTask? (replace by isFake)
////  val searchEngine: SearchEngine.Value,
//  val paramMap: JsObject,
//  val isTemplate: Boolean = false,
//
//  /** For templates only */
//  val name: Option[String] = None,
//  val ownerMongoId: Option[String] = None, 
//  val registTime: Option[DateTime] = None
//  
//) {
//  //extends IMsiObject {
//
//  /** Requirements */
//  require(targetURL != null, "targetURL must not be null.")
//  require(targetURL.isEmpty() == false, "targetURL must not be empty.")
////  require(searchEngine != null, "Search engine must not be null")
//  require(paramMap != null, "Some paramMap must be provided")
//
//  /** Some class utilities */
//  def get(key: MascotSearchParam.Value): String = ((paramMap \ key.toString()).asOpt[String]).getOrElse("")
//  def getOpt(key: MascotSearchParam.Value): Option[String] = (paramMap \ key.toString()).asOpt[String]
//  
//}

// BEFORE REFACTORING
