package fr.profi.msangel.om.workflow.operation

import scala.BigDecimal
import scala.collection.mutable.ArrayBuffer

import com.typesafe.scalalogging.slf4j.Logging

import play.api.libs.json._

import fr.profi.msangel.om.DataFileFormat
import fr.profi.msangel.om.FileConversionTool
import fr.profi.msangel.om.implicits._
import fr.profi.util.scala.BigDecimalRange

/**
 * Describes file conversion tools
 */
//TODO : rename file

trait IFileConversionTool {
  
  def getConfigTemplate( ): ConversionToolConfig
  
  def checkExePath (conversionToolPath : String ) : Boolean
  
  def generateCmdLine(filePath: String, conversionToolPath: String, fileConversion: FileConversion): String
}

case class ConversionToolConfig(
  //  val id: String, //Mongo id
  val tool: FileConversionTool.Value,
  val formatMappings: Array[(DataFileFormat.Value, DataFileFormat.Value)],
  //  val inputFileFormat: DataFileFormat.Value, 
  //  val outputFileFormat: DataFileFormat.Value,
  val params: Array[MacroParam] = Array(),
  val filters: Array[MacroFilterParam] = Array()
) {

  require(formatMappings != null && formatMappings.length >= 1, "Macro must have at least one IO formats couple.")
  require(formatMappings.forall { case (i, o) => i != o }, "Macro input and output formats must be different.")
  //  require(inputFileFormat != null, "Macro initFileFormat must be specified.")
  //  require(outputFileFormat != null, "Macro finalFileFormat must be specified.")
  //  require(inputFileFormat != outputFileFormat, "Initial and final file formats can't be the same")

  override def toString(): String = this.tool
  //  override def toString: String = scala.runtime.ScalaRunTime.stringOf(params) + "\n"

  //  def getParamsAsStringMap(): Map[String, String] = {
  //    params.map { param => (param.name, param.value.getOrElse("").toString()) }.toMap
  //  }

  /**
   * Filters utilities
   */

  def hasFilters: Boolean = !filters.isEmpty

  /** User defined filters as Strings (cmd) */
  private val definedFilters = ArrayBuffer[String]()
  def addFilter(filterAsStr: String) { definedFilters += filterAsStr }
  def getFiltersAsStrings() = definedFilters.result()

  def cloneMe(): ConversionToolConfig = {

    val paramsClone = params.clone()
    for ((p, i) <- paramsClone.zipWithIndex) {
      paramsClone(i) = p.cloneMe()
    }

    val filtersClone = filters.clone()
    for ((f, i) <- filtersClone.zipWithIndex) {
      filtersClone(i) = f.cloneMe()
    }

    this.copy(params = paramsClone, filters = filtersClone)
  }
}

/**
 * Accepted types of parameters
 */
object MacroParamType extends JsonEnumeration {
  val BOOLEAN = Value("BOOLEAN")
  val STRING = Value("STRING")
  val NUMERIC = Value("NUMERIC")
  val RANGE = Value("RANGE")
  val CHOICE = Value("CHOICE")
  val FILTER = Value("FILTER")
}

/**
 * Handles serialization & de-serialization of parameters
 */
object MacroParam {

  def apply(
    name: String,
    paramTypeAsStr: String,
    isRequired: Boolean,
    cmdFlag: String,
    value: Option[JsValue],
    default: Option[JsValue],
    options: Option[JsArray]): MacroParam = {

    val paramType = MacroParamType.withName(paramTypeAsStr)

    val macroParam: MacroParam = paramType match {
      case MacroParamType.BOOLEAN => {
        new MacroBooleanParam(name, cmdFlag, value.map(_.as[Boolean]), default.map(_.as[Boolean]))
      }
      case MacroParamType.NUMERIC => {
        new MacroNumericParam(name, isRequired, cmdFlag, value.map(_.as[BigDecimal]), default.map(_.as[BigDecimal]), options.map(_.as[Seq[BigDecimal]]))
      }
      case MacroParamType.STRING => {
        new MacroStringParam(name, isRequired, cmdFlag, value.map(_.as[String]), default.map(_.as[String]), options.map(_.as[Seq[String]]))
      }
      case MacroParamType.RANGE => {
        //new MacroRangeParam(name, isRequired, cmdFlag, value.map(_.as[Range]), default.map(_.as[Range]))
        new MacroRangeParam(name, isRequired, cmdFlag, value.map(_.as[BigDecimalRange]), default.map(_.as[BigDecimalRange]))
      }
      case MacroParamType.CHOICE => {
        new MacroChoiceParam(name, value.map(_.as[MacroChoiceParamItem]), default.map(_.as[MacroChoiceParamItem]), options.map(_.as[Seq[MacroChoiceParamItem]]))
      }
      //      case MacroParamType.FILTER => {
      //        new MacroFilterParam(name, default.map(_.as[Seq[MacroParam]]), options.map(_.as[Seq[MacroParam]]))
      //      }
      case _ => throw new Exception("error when parsing MacroParamList")
    }

    macroParam
  }

  def unapply(par: MacroParam): Option[(String, String, Boolean, String, Option[JsValue], Option[JsValue], Option[JsArray])] = {
    val tuple = par match {

      case p: MacroBooleanParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray])
      )

      case p: MacroStringParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray])
      )

      case p: MacroNumericParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray])
      )

      case p: MacroRangeParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        //        p.value.map(Json.toJson(_)),
        //        p.default.map(Json.toJson(_)),
        None
      )

      case p: MacroChoiceParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        "",
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray])
      )

      case _ => throw new Exception("Exception in MacroParam unapply")
    }

    Some(tuple)
  }
}

/**
 * Model for all types of parameters
 */
sealed trait MacroParam { //extends Cloneable
  def name: String
  def paramType: MacroParamType.Value
  def isRequired: Boolean
  def cmdFlag: String // "-o" || "--opt"
  def value: Option[Any]
  def default: Option[Any]
  def options: Option[Seq[Any]]

  /** For all implementations */
  require(name != null, "Parameter name must be provided")

  override def toString(): String = this.name

  def getOptionsAsStrings() = options.map(_.map(_.toString)).getOrElse(Seq())
  //  def getOptionsAsBigDecimals() = options.map(_.map(_.asInstanceOf[BigDecimal])).getOrElse(Seq())

  /** To be defined in each implementation */
  def setValue(valueAsStr: String)

  def cloneMe(): MacroParam
}

/**
 *  MacroParam implementations
 */

/** Boolean parameter */
case class MacroBooleanParam(
  val name: String,
  val cmdFlag: String,
  var value: Option[Boolean] = None,
  val default: Option[Boolean] = Some(false)
) extends MacroParam {

  val isRequired = false
  val paramType = MacroParamType.BOOLEAN
  val options: Option[Seq[Boolean]] = None

  /** Make sure there is always a value */
  require(default.isDefined, "default choice item must be defined")
  if (value.isEmpty) value = default

  def setValue(valueAsStr: String) = {
    if (valueAsStr == null || valueAsStr.isEmpty) value = None
    else value = Some(valueAsStr.toBoolean)
  }

  def cloneMe() = this.copy()

  /*def setValue(valueAsBoolean: Boolean) {
    value = Some(valueAsBoolean)
  }*/
}

/** String parameter */
case class MacroStringParam(
  val name: String,
  val isRequired: Boolean,
  val cmdFlag: String,
  var value: Option[String] = None,
  val default: Option[String] = None,
  val options: Option[Seq[String]] = None) extends MacroParam {

  val paramType = MacroParamType.STRING

  def setValue(valueAsStr: String) = {
    if (valueAsStr == null || valueAsStr.isEmpty) value = None
    else value = Some(valueAsStr)
  }

  def cloneMe() = this.copy()
}

/** Numeric parameter */
case class MacroNumericParam(
  val name: String,
  val isRequired: Boolean,
  val cmdFlag: String,
  var value: Option[BigDecimal] = None,
  val default: Option[BigDecimal] = None,
  val options: Option[Seq[BigDecimal]] = None) extends MacroParam {

  val paramType = MacroParamType.NUMERIC

  def setValue(valueAsStr: String) = {
    if (valueAsStr == null || valueAsStr.isEmpty) value = None
    else value = Some(BigDecimal(valueAsStr))
  }

  def cloneMe() = this.copy()
}

/** Range parameter (numeric) */
case class MacroRangeParam(
  val name: String,
  val isRequired: Boolean = false,
  val cmdFlag: String,
  //  var value: Option[Range] = None,
  //  val default: Option[Range] = None
  var value: Option[BigDecimalRange] = None,
  val default: Option[BigDecimalRange] = None) extends MacroParam with Logging {

  val paramType = MacroParamType.RANGE
  val options = None

  /** WARNING : strings defining macro range MUST be of type "val1#val2" */
  def setValue(valueAsStr: String) {

    if (valueAsStr == null || valueAsStr.isEmpty) value = None

    val split = valueAsStr.split("#")
    if (split.length != 2) value = None
    else {
      this.setValue(Some(BigDecimal(split.head)), Some(BigDecimal(split.last)))
    }
  }

  def setValue(min: Option[BigDecimal] = None, max: Option[BigDecimal] = None) = {
    this.value = Some(min, max)
  }

  def cloneMe() = this.copy()
}

/** Choose between options described by a parameter */
case class MacroChoiceParamItem(name: String, cmdFlag: String) {
  override def toString(): String = this.name
}
case class MacroChoiceParam(
  val name: String,
  var value: Option[MacroChoiceParamItem] = None,
  val default: Option[MacroChoiceParamItem] = None,
  val options: Option[Seq[MacroChoiceParamItem]]) extends MacroParam with Logging {

  val paramType = MacroParamType.CHOICE
  val isRequired: Boolean = false

  require(options.isDefined && options.get.isEmpty == false, "Choice options MUST be defined")

  if (value.isEmpty) {
    if (default.isDefined) {
      require(options.get.contains(default.get), "default item must be a member of options")
      value = default
    } else value = options.get.headOption
  }

  def cmdFlag: String = this.value.map(_.cmdFlag).getOrElse("")

  /** WARNING : strings defining macro range MUST be of type "val1#val2" */
  def setValue(valueAsStr: String) {

  }

  def setValue(value: MacroChoiceParamItem) {
    this.value = Some(value)
  }

  def cloneMe() = this.copy()
}

/** Filter parameter */
case class MacroFilterParam(
  val name: String,
  val cmdFlag: String,
  var value: Option[Seq[MacroParam]] = None,
  val default: Option[Seq[MacroParam]] = None,
  val options: Option[Seq[MacroParam]] //contains filter's parameters as MacroParams
  //  ) extends MacroParam { //??
  ) {

  //  require(options.isDefined, "Filter must have parameters")

  val paramType = MacroParamType.FILTER
  val isRequired = false

  def setValue(valueAsStr: String) {} //TODO

  override def toString(): String = this.name

  def cloneMe() = this.copy()
}
