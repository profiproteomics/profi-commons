package fr.profi.msangel.om.workflow.operation.conversion

import java.io.File

import scala.collection.mutable.ArrayBuffer

import fr.profi.msangel.om._
import fr.profi.msangel.om.DataFileExtension
import fr.profi.msangel.om.DataFileExtension._
import fr.profi.msangel.om.workflow.DefaultPeaklistSoftware
import fr.profi.msangel.om.workflow.operation._

object MsDataConverter extends IFileConversionTool {

  def getName(): FileConversionTool.Value = FileConversionTool.MS_DATA_CONVERTER
  lazy val supportedVersion = Some("beta")
  lazy val successExitValue = 0
  lazy val canExecuteProlineParsingRule = false
  lazy val associatedPeaklistSoftware = Some(DefaultPeaklistSoftware.PROTEIN_PILOT)

  /**
   * List all output formats handled by MS Data Converter, linked to their command flag
   */
  import DataFileExtension._

  private val cmdFlagByOutputFormat = Map[DataFileExtension.Value, String](
    MGF -> "MGF",
    MZML -> "MZML"
  )

  /**
   * Get MS Data Converter default configuration template
   */
  object ParamName {
    val PEAK_PICKING = "Peak picking"
    val COMPRESSION = "Compression (mzML only)"
    val PRECISION = "Precision (mzML only)"
    val CREATE_INDEX = "Create index (mzML only)"
  }

  val profile = new MacroChoiceParamItem("Profile data (mzML only)", "-profile")
  val instruCentroid = new MacroChoiceParamItem("Instrument centroiding", "-centroid")
  val proteinPilotCentroid = new MacroChoiceParamItem("ProteinPilot centroiding", "-proteinpilot")

  val noCompression = new MacroChoiceParamItem("No compression", "/nocompression")
  val zlib = new MacroChoiceParamItem("zlib compression", "/zlib")

  val bits64 = new MacroChoiceParamItem("64-bit float double precision", "/doubleprecision")
  val bits32 = new MacroChoiceParamItem("32-bit float single precision", "/singleprecision")

  def getConfigTemplate(): ConversionToolConfig = {

    new ConversionToolConfig(
      tool = this.getName(),
      toolVersion = this.supportedVersion,

      params = Array(
          
        MacroChoiceParam(
          name = ParamName.PEAK_PICKING,
          default = Some(proteinPilotCentroid),
          value = Some(proteinPilotCentroid), //here we set the value because we chose a default value different that exe default, so it needs to be explicit
          options = Some(Seq(profile, instruCentroid, proteinPilotCentroid))
        ),
        MacroChoiceParam(
          name = ParamName.COMPRESSION,
          default = Some(zlib),
          options = Some(Seq(zlib, noCompression))
        ),
        MacroChoiceParam(
          name = ParamName.PRECISION,
          default = Some(bits64),
          options = Some(Seq(bits32, bits64))
        ),
        MacroBooleanParam(
          name = ParamName.CREATE_INDEX, 
          cmdFlag = "/index",
          default = Some(true)
        )
      )
    )
  }

  /**
   * Generate all input/output formats associations handled by MsConvert
   */
  def getFormatMappings(): Array[(DataFileExtension.Value, DataFileExtension.Value)] =
    cmdFlagByOutputFormat.keySet.map((WIFF, _)).toArray ++ cmdFlagByOutputFormat.keySet.map((TOFTOF, _))

  /**
   *  Check tool path conformity
   */
  def checkExePath(conversionToolPath: String): Boolean = {

    val containsExe = conversionToolPath.endsWith("AB_SCIEX_MS_Converter.exe") //? can be renamed...
    val exeExistsAtPath = new File(conversionToolPath).exists()
    //    println("containsExe: " + containsExe)
    //    println("exeAtPath: " + exeAtPath)

    if (containsExe && exeExistsAtPath) true else false
    //    require(containsExe, "Exe path must finish by MS Data Converter.exe") //? can be renamed...
    //    require(exeAtPath, "MS Data Converter.exe can't be found at specified path")
  }

  /**
   *  Command line generator
   */
  def generateCmdLine(
    inputFilePath: String,
    outputFilePath: String,
    conversionToolPath: String,
    fileConversion: FileConversion,
    javaArgs:Array[String] = Array()
  ): String = {

    val paramByName = fileConversion.config.params.map(p => p.name -> p).toMap
    val cmdLineBuffer = new ArrayBuffer[String]()

    /* Executable */
    cmdLineBuffer += s""""${conversionToolPath}"""" //MS Data Converter.exe path

    /* Input format */
    cmdLineBuffer += fileConversion.inputFileFormat

    /* Input file */
    cmdLineBuffer += s""""$inputFilePath"""" //input file path

    /* Output content type */
    val outputContentTypeOpt = paramByName(ParamName.PEAK_PICKING).asInstanceOf[MacroChoiceParam].value
    require(outputContentTypeOpt.isDefined, "outputContentType must be defined")
    cmdLineBuffer += outputContentTypeOpt.get.cmdFlag
    
    /* Output format */
    cmdLineBuffer += fileConversion.outputFileFormat
    /*paramByName(ParamName.PEAK_LIST_FORMAT).asInstanceOf[MacroChoiceParam].value
    require( outputFormatOpt.isDefined, "outputFormat must be defined")
    cmdLineBuffer += outputFormatOpt.get.cmdFlag*/

    /* Output file */
    cmdLineBuffer += s""""$outputFilePath""""

    /* Additional conversion parameters (mzML only) */
    if (fileConversion.outputFileFormat == MZML) {
      for (
        param <- fileConversion.config.params;
        if param.value.isDefined;
        if param.name != ParamName.PEAK_PICKING
      ) {
        param match {

          case boolean: MacroBooleanParam => {
            if (boolean.value.get) cmdLineBuffer += boolean.cmdFlag
          }

          case choice: MacroChoiceParam => {
            cmdLineBuffer += choice.cmdFlag
          }

          case _ => throw new Exception("Invalid parameter type for this tool: " + param)
        }
      }
    }

    /* Build and return final command string **/
    cmdLineBuffer.mkString(" ")
  }
}