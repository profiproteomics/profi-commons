package fr.profi.msangel.om.workflow.operation

import org.cvogt.play.json.implicits.optionNoError

import com.typesafe.scalalogging.LazyLogging

import scala.BigDecimal
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

import play.api.libs.json._

import fr.profi.msangel.om._
import fr.profi.msangel.om.DataFileExtension
import fr.profi.msangel.om.FileConversionTool
import fr.profi.msangel.om.workflow.PeaklistSoftware
import fr.profi.util.scala.BigDecimalRange
import fr.profi.util.scala.CheckResult

/**
 * Describes file conversion tools
 */
//TODO : rename file

trait IFileConversionTool {
  
  def getName(): FileConversionTool.Value
  
  def getConfigTemplate(): ConversionToolConfig
  
  def getFormatMappings(): Array[(DataFileExtension.Value, DataFileExtension.Value)]

  def checkExePath(conversionToolPath: String): Boolean
  
  def generateCmdLine(inputFilePath: String, outputFilePath: String, conversionToolPath: String, fileConversion: FileConversion, javaArgs:Array[String] = Array()): String
  
  def supportedVersion: Option[String]

  def successExitValue: Int

  def canExecuteProlineParsingRule: Boolean

  def associatedPeaklistSoftware: Option[PeaklistSoftware]
  
  //  def getOutputFileFromSTDOUT( stdOut : String ) : Option[String]
}

/**
 *  Register all file conversion tool (default configurations)
 */
object FileConversionToolRegistry {

  private val toolHashMap = new HashMap[FileConversionTool.Value, IFileConversionTool]()
  
  // Register default conversion tool configs
  this.registerFileConversionTool(conversion.ExtractMSn)
  this.registerFileConversionTool(conversion.MsConvert)
  this.registerFileConversionTool(conversion.MsDataConverter)
  this.registerFileConversionTool(conversion.MzdbAccess)
  this.registerFileConversionTool(conversion.Raw2mzDB)

  def registerFileConversionTool(tool: IFileConversionTool): Unit = {
    toolHashMap += tool.getName -> tool
  }

  def getConversionToolConfig(toolName: FileConversionTool.Value): Option[IFileConversionTool] = {
    toolHashMap.get(toolName)
  }

}

/**
 * Define a conversion tool's configuration
 */
case class ConversionToolConfig(
  val tool: FileConversionTool.Value,
  val toolVersion: Option[String] = None,
  val params: Array[MacroParam] = Array(),
  val filters: Array[MacroFilterParam] = Array()
) {
  
  def getTool(): IFileConversionTool = FileConversionToolRegistry.getConversionToolConfig(tool).get
  def getFormatMappings(): Array[(DataFileExtension.Value, DataFileExtension.Value)] = this.getTool().getFormatMappings

  override def toString(): String = this.tool
  //  override def toString: String = scala.runtime.ScalaRunTime.stringOf(params) + "\n"

  //  def getParamsAsStringMap(): Map[String, String] = {
  //    params.map { param => (param.name, param.value.getOrElse("").toString()) }.toMap
  //  }

  /** Filters utilities **/
  def hasFilters: Boolean = !filters.isEmpty

  /** User defined filters as Strings (cmd) */
  private val definedFilters = ArrayBuffer[String]()
  def addFilter(filterAsStr: String) { definedFilters += filterAsStr }
  def getFiltersAsStrings() = definedFilters.result()

  /** Clone this object **/
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
  val SELECTION = Value("SELECTION")
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
    description: Option[String],
    value: Option[JsValue],
    default: Option[JsValue],
    options: Option[JsArray],
    minValue: Option[BigDecimal],
    maxValue: Option[BigDecimal],
    allowLeftMemberEmpty: Option[Boolean],
    allowRightMemberEmpty: Option[Boolean]
  ): MacroParam = {

    val paramType = MacroParamType.withName(paramTypeAsStr)

    val macroParam: MacroParam = paramType match {
      case MacroParamType.BOOLEAN => {
        new MacroBooleanParam(name, cmdFlag, description, value.map(_.as[Boolean]), default.map(_.as[Boolean]))
      }
      case MacroParamType.STRING => {
        new MacroStringParam(name, isRequired, cmdFlag, description, value.map(_.as[String]), default.map(_.as[String]), options.map(_.as[Seq[String]]))
      }
      case MacroParamType.NUMERIC => {
        new MacroNumericParam(name, isRequired, cmdFlag, description, value.map(_.as[BigDecimal]), default.map(_.as[BigDecimal]), options.map(_.as[Seq[BigDecimal]]), minValue, maxValue)
      }
      case MacroParamType.RANGE => {
        new MacroRangeParam(name, isRequired, cmdFlag, description, value.map(_.as[BigDecimalRange]), default.map(_.as[BigDecimalRange]), minValue, maxValue, allowLeftMemberEmpty, allowRightMemberEmpty)
      }
      case MacroParamType.CHOICE => {
        new MacroChoiceParam(name, isRequired, description, value.map(_.as[MacroChoiceParamItem]), default.map(_.as[MacroChoiceParamItem]), options.map(_.as[Seq[MacroChoiceParamItem]]))
      }
      case MacroParamType.SELECTION => {
        new MacroSelectionParam(name, isRequired, cmdFlag, description, value.map(_.as[MacroSelectionParamItem]), default.map(_.as[MacroSelectionParamItem]), options.map(_.as[Seq[MacroSelectionParamItem]]))
      }
      //      case MacroParamType.FILTER => {
      //        new MacroFilterParam(name, description, default.map(_.as[Seq[MacroParam]]), options.map(_.as[Seq[MacroParam]]))
      //      }
      case _ => throw new Exception("error when parsing MacroParamList")
    }

    macroParam
  }

  def unapply(par: MacroParam): Option[(String, String, Boolean, String, Option[String], Option[JsValue], Option[JsValue], Option[JsArray], Option[BigDecimal], Option[BigDecimal], Option[Boolean], Option[Boolean])] = {
    val tuple = par match {

      case p: MacroBooleanParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.description,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray]),
        None,
        None,
        None,
        None
      )

      case p: MacroStringParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.description,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray]),
        None,
        None,
        None,
        None
      )

      case p: MacroNumericParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.description,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray]),
        p.minValue,
        p.maxValue,
        None,
        None
      )

      case p: MacroRangeParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.description,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        None,
        p.minValue,
        p.maxValue,
        p.allowLeftMemberEmpty,
        p.allowRightMemberEmpty
      )

      case p: MacroChoiceParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        "",
        p.description,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray]),
        None,
        None,
        None,
        None
      )

      case p: MacroSelectionParam => (
        p.name,
        p.paramType.toString,
        p.isRequired,
        p.cmdFlag,
        p.description,
        p.value.map(Json.toJson(_)),
        p.default.map(Json.toJson(_)),
        p.options.map(Json.toJson(_).as[JsArray]),
        None,
        None,
        None,
        None
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
  def description: Option[String]
  def value: Option[Any]
  def default: Option[Any]
  def options: Option[Seq[Any]]

  // For numeric and range params
  def minValue: Option[BigDecimal]
  def maxValue: Option[BigDecimal]
  def allowLeftMemberEmpty: Option[Boolean] //TODO: only boolean
  def allowRightMemberEmpty: Option[Boolean]
    
  /* For all implementations */
  require(name != null, "Parameter name must be provided")

  override def toString(): String = this.name //+ "-" + this.paramType

  def getOptionsAsStrings() = options.map(_.map(_.toString)).getOrElse(Seq())
  //  def getOptionsAsBigDecimals() = options.map(_.map(_.asInstanceOf[BigDecimal])).getOrElse(Seq())
  
  def getValueOrDefaultAsString(): String = value.getOrElse(default.getOrElse("")).toString

  protected def _checkRequiredParam(checkResult: CheckResult): CheckResult = {
    if (isRequired && value.isEmpty) checkResult.addError(s"\n\n* Param: '$name'\nError= Required conversion parameter is not provided.")
    checkResult
  }

  def checkParam(): CheckResult = {
    _checkRequiredParam(
      new CheckResult(Some(1))
    )
  }

  /* To be defined in each implementation */
  def setValue(valueAsStr: String)
  def setValueAsDefault(): Unit

  def cloneMe(): MacroParam
}

/**
 *  MacroParam implementations
 */

/** Boolean parameter */
case class MacroBooleanParam(
  val name: String,
  val cmdFlag: String,
  val description: Option[String] = None,
  var value: Option[Boolean] = None,
  val default: Option[Boolean] = Some(false)
) extends MacroParam {

  val isRequired = false
  val paramType = MacroParamType.BOOLEAN
  val options: Option[Seq[Boolean]] = None
  val minValue: Option[BigDecimal] = None
  val maxValue: Option[BigDecimal] = None
  val allowLeftMemberEmpty: Option[Boolean] = None
  val allowRightMemberEmpty: Option[Boolean] = None

  /** Make sure there is always a value */
  require(default.isDefined, "default choice item must be defined")
  if (value.isEmpty) value = default

  def setValue(valueAsStr: String) = {
    if (valueAsStr == null || valueAsStr.isEmpty) value = None
    else value = Some(valueAsStr.toBoolean)
  }
  
  def setValueAsDefault() { value = default }

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
  val description: Option[String] = None,
  var value: Option[String] = None,
  val default: Option[String] = None,
  val options: Option[Seq[String]] = None
) extends MacroParam {

  val paramType = MacroParamType.STRING
  val minValue: Option[BigDecimal] = None
  val maxValue: Option[BigDecimal] = None
  val allowLeftMemberEmpty: Option[Boolean] = None
  val allowRightMemberEmpty: Option[Boolean] = None

  def setValue(valueAsStr: String) = {
    if (valueAsStr == null || valueAsStr.isEmpty) value = None
    else value = Some(valueAsStr)
  }
  
  def setValueAsDefault() { value = default }

  def cloneMe() = this.copy()
}

/** Numeric parameter */
case class MacroNumericParam(
  val name: String,
  val isRequired: Boolean,
  val cmdFlag: String,
  val description: Option[String] = None,
  var value: Option[BigDecimal] = None,
  val default: Option[BigDecimal] = None,
  val options: Option[Seq[BigDecimal]] = None,
  val minValue: Option[BigDecimal] = None,
  val maxValue: Option[BigDecimal] = None
) extends MacroParam {

  val paramType = MacroParamType.NUMERIC
  val allowLeftMemberEmpty: Option[Boolean] = None
  val allowRightMemberEmpty: Option[Boolean] = None

  def setValue(valueAsStr: String) = {
    if (valueAsStr == null || valueAsStr.isEmpty) value = None
    else value = Some(BigDecimal(valueAsStr))
  }

  def setValueAsDefault() { value = default }

  override def checkParam(): CheckResult = {
    val checkResult = new CheckResult(Some(2))

    _checkRequiredParam(checkResult)

    if (value.isDefined) {
      val bigD = value.get
      val debugString = s"\n\n* Param: '$name'\nValue= $bigD\nError= "
      if (minValue.isDefined && bigD < minValue.get) checkResult.addError(debugString + s"value inferior to minimum (min=${minValue.get})")
      if (maxValue.isDefined && bigD > maxValue.get) checkResult.addError(debugString + s"Value superior to maximum (max=${maxValue.get})")
    }
    checkResult
  }

  def cloneMe() = this.copy()
}

/** Range parameter (numeric) */
case class MacroRangeParam(
  val name: String,
  val isRequired: Boolean = false,
  val cmdFlag: String,
  val description: Option[String] = None,
  var value: Option[BigDecimalRange] = None,
  val default: Option[BigDecimalRange] = None,
  val minValue: Option[BigDecimal] = None,
  val maxValue: Option[BigDecimal] = None,
  val allowLeftMemberEmpty: Option[Boolean] = Some(false),
  val allowRightMemberEmpty: Option[Boolean] = Some(false)
) extends MacroParam with LazyLogging {

  val paramType = MacroParamType.RANGE
  val options = None
  
  override def getValueOrDefaultAsString(): String = {
    val valueOrDefaultOpt = if(value.isDefined) value else default
    valueOrDefaultOpt.map( range => range._1.getOrElse("") + " to " + range._2.getOrElse("") ).getOrElse("")
  }

  // WARNING : strings defining macro range MUST be of type "val1#val2"
  def setValue(valueAsStr: String) {

    if (valueAsStr == null || valueAsStr.isEmpty) value = None

    val split = valueAsStr.split("#")
    if (split.length != 2) value = None
    //if (_allowEmptyMax == false && split.length != 2) value = None
    else {
      this.setValue(Some(BigDecimal(split.head)), Some(BigDecimal(split.last)))
    }
  }

  def setValue(min: Option[BigDecimal] = None, max: Option[BigDecimal] = None) = {
    this.value = Some(min, max)
  }
  
  def setValue(rangeOpt: Option[BigDecimalRange]) {
    this.value = rangeOpt
  }
  
  def setValueAsDefault() { value = default }

  def cloneMe() = this.copy()

  override def checkParam(): CheckResult = {

    val checkResult = new CheckResult()

    _checkRequiredParam(checkResult)

    if (value.isDefined) {
      val range = value.get
      val (minOpt, maxOpt) = range
      val debugRangeAndNameString = s"\n\n* Param: '$name'\nRange= ${minOpt.getOrElse("")}-${maxOpt.getOrElse("")}\nError= "

      // Test min value
      if (allowLeftMemberEmpty.isDefined) {
        if (minOpt.isEmpty && !allowLeftMemberEmpty.get)
          checkResult.addError(debugRangeAndNameString + "Interval lower bound required but not provided")
      }

      if (minOpt.isDefined) {
        val min = minOpt.get
        if (minValue.isDefined && min < minValue.get)
          checkResult.addError(debugRangeAndNameString + s"Interval lower bound is inferior to minimal value (min=${minValue.get})")
        if (maxValue.isDefined && min > maxValue.get)
          checkResult.addError(debugRangeAndNameString + s"Interval lower bound is superior to maximal value (min=${maxValue.get})")
      }

      // Test max value
      if (allowRightMemberEmpty.isDefined) {
        if (maxOpt.isEmpty && !allowRightMemberEmpty.get)
          checkResult.addError(debugRangeAndNameString + s"Interval upper bound required but not provided")
      }
            
      if (maxOpt.isDefined && maxValue.isDefined) {
        val max = maxOpt.get
        if (minValue.isDefined && max < minValue.get)
          checkResult.addError(debugRangeAndNameString + s"Interval upper bound is inferior to minimal value (min=${minValue.get})")
        if (maxValue.isDefined && max > maxValue.get)
          checkResult.addError(debugRangeAndNameString + s"Interval upper bound is superior to maximal value (min=${maxValue.get})")
      }

      // Test min > max
      if (minOpt.isDefined && maxOpt.isDefined && minOpt.get > maxOpt.get)
        checkResult.addError(debugRangeAndNameString + "Interval lower bound is superior to upper bound")
    }

    checkResult
  }

}

/** A trait for macro parameter items of all types */
trait IMacroParamItem {
  def name: String
  
  // For graphical purposes (display only item name, not the full object)
  override def toString(): String = this.name
} 

/** A parameter option item, providing a specific command flag */
case class MacroChoiceParamItem(name: String, cmdFlag: String) extends IMacroParamItem

/** 
 *  Choice parameter: choose between options giving their cmdFlag.
 *  Each param item has got its own flag. In the end, only the flag of the selected item will be passed to command line.
 *  Will be interfaced in GUI with a RadioBox if they are 2 options, otherwise with a ComboBox.
 */
case class MacroChoiceParam(
  val name: String,
  val isRequired: Boolean = false,
  val description: Option[String] = None,
  var value: Option[MacroChoiceParamItem] = None,
  val default: Option[MacroChoiceParamItem] = None,
  val options: Option[Seq[MacroChoiceParamItem]]
) extends MacroParam with LazyLogging {

  val paramType = MacroParamType.CHOICE //only used for filters
  val minValue: Option[BigDecimal] = None
  val maxValue: Option[BigDecimal] = None
  val allowLeftMemberEmpty: Option[Boolean] = None
  val allowRightMemberEmpty: Option[Boolean] = None

  require(options.isDefined && options.get.isEmpty == false, "Choice options MUST be defined")

  if (value.isEmpty) {
    if (default.isDefined) {
      require(options.get.contains(default.get), "default item must be a member of options")
      value = default
    } else value = options.get.headOption
  }

  def cmdFlag: String = this.value.map(_.cmdFlag).getOrElse("")

  def setValue(valueAsStr: String) { }

  def setValue(value: MacroChoiceParamItem) {
    this.value = Some(value)
  }
  
  def setValueAsDefault() { value = default }

  def cloneMe() = this.copy()
}

/** A parameter option item, providing a specific value */
case class MacroSelectionParamItem(name: String, value: String) extends IMacroParamItem

/**
 * Selection parameter: choose between options giving their value.
 * Each option offers a value to associate with the parameter flag in the command line.
 * Will be interfaced with a ComboBox in GUI.
 */
case class MacroSelectionParam(
  val name: String,
  val isRequired: Boolean = false,
  val cmdFlag: String,
  val description: Option[String] = None,
  var value: Option[MacroSelectionParamItem] = None,
  val default: Option[MacroSelectionParamItem] = None,
  val options: Option[Seq[MacroSelectionParamItem]]
) extends MacroParam with LazyLogging {

  val paramType = MacroParamType.SELECTION //only used for filters
  val minValue: Option[BigDecimal] = None
  val maxValue: Option[BigDecimal] = None
  val allowLeftMemberEmpty: Option[Boolean] = None
  val allowRightMemberEmpty: Option[Boolean] = None

  require(options.isDefined && options.get.isEmpty == false, "Selection options MUST be defined")

  if (value.isEmpty) {
    if (default.isDefined) {
      require(options.get.contains(default.get), "default item must be a member of options")
      value = default
    } else value = options.get.headOption
  }

  def setValue(valueAsStr: String) { }

  def setValue(value: MacroSelectionParamItem) {
    this.value = Some(value)
  }
  
  def setValueAsDefault() { value = default }

  def cloneMe() = this.copy()
}


/** Filter parameter */
case class MacroFilterParam(
  val name: String,
  val cmdFlag: String,
  val description: Option[String] = None,
  var value: Option[Seq[MacroParam]] = None,
  val default: Option[Seq[MacroParam]] = None,
  val options: Option[Seq[MacroParam]] //contains filter's parameters as MacroParams
  //  ) extends MacroParam { //??
  ) {

  //  require(options.isDefined, "Filter must have parameters")

  val paramType = MacroParamType.FILTER
  val isRequired = false

  def setValue(valueAsStr: String) {} //TODO
  
  def setValueAsDefault() { value = default }

  override def toString(): String = this.name

  def cloneMe() = this.copy()
}
