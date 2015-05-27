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
  
  /** Get text to display easily parameters **/
  def getParamsAsString(): String = {

    val MSP = MascotSearchParam

    /* Utility */
    def _param2string(param: MSP.Value): String = {
      val str = ((this.paramMap \ param.toString()).asOpt[String]).getOrElse("")
      if (param == MascotSearchParam.DECOY || param == MascotSearchParam.ERRORTOLERANT) {
        if (str == "1") "true" else "false"
      } else str
    }

    val strBld = new StringBuilder()
    
    /* Parameters common to all searches */
    strBld ++= s"""
Database(s) : ${_param2string(MSP.DB)}
Taxonomy : ${_param2string(MSP.TAXONOMY).dropWhile { c => c.toString() == "." || c.toString() == " " }}
Cleavage enzyme: ${_param2string(MSP.CLE)}
Fixed modifications : ${_param2string(MSP.MODS)}
Variable modifications : ${_param2string(MSP.IT_MODS)}
Protein mass : ${_param2string(MSP.SEG)} (kDa)
Max. missed cleavages : ${_param2string(MSP.PFA)}
Peptide mass tolerance : ${_param2string(MSP.TOL)} ${_param2string(MSP.TOLU)}
Peptide charge : ${_param2string(MSP.CHARGE)}
Precursor m/z : ${_param2string(MSP.PRECURSOR)}
Misassigned 13C : ${_param2string(MSP.PEP_ISOTOPE_ERROR)}
Report : ${_param2string(MSP.REPORT)} top hits
Decoy : ${_param2string(MSP.DECOY)}
Mass : ${_param2string(MSP.MASS)}
"""
    //TODO: add project

    /* Add MS/MS parameters if applicable */
    if (_param2string(MSP.SEARCH) == "MIS") strBld ++= s"""
MS/MS IONS SEARCH : true
Error tolerant : ${_param2string(MSP.ERRORTOLERANT)}
MS/MS tolerance : ${_param2string(MSP.ITOL)} ${_param2string(MSP.ITOLU)}
Data format : ${_param2string(MSP.FORMAT)}
Quantitation : ${_param2string(MSP.QUANTITATION)}
Instrument : ${_param2string(MSP.INSTRUMENT)}
"""
    /* Otherwise say it's not an MS/MS search */
    else strBld ++= """
MS/MS IONS SEARCH : false
"""

    strBld.result()
  }
}

/**
 * Provide default Mascot searchForm with no parameter
 */
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