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
  val successExitValue = 0
  val canExecuteProlineParsingRule = false
  val associatedPeaklistSoftware = Some(DefaultPeaklistSoftware.PROTEIN_PILOT)

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
    //val PEAK_LIST_FORMAT = "Peak list format"
    val COMPRESSION = "Compression (mzML only)"
    val PRECISION = "Precision (mzML only)"
    val CREATE_INDEX = "Create index (mzML only)"
  }

  // TODO? store into object?
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
      params = Array(
          
        MacroSelectionParam(
          name = ParamName.PEAK_PICKING,
          default = Some(instruCentroid),
          options = Some(Seq(profile, instruCentroid, proteinPilotCentroid))
        ),
        MacroSelectionParam(
          name = ParamName.COMPRESSION,
          default = Some(zlib),
          options = Some(Seq(zlib, noCompression))
        ),
        MacroSelectionParam(
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
    conversionToolPath: String,
    fileConversion: FileConversion
  ): String = {

    val paramByName = fileConversion.config.params.map(p => p.name -> p).toMap
    val cmdLineBuffer = new ArrayBuffer[String]()

    /* Executable */
    cmdLineBuffer += s""""${conversionToolPath}"""" //MS Data Converter.exe path

    /* Input format */
    cmdLineBuffer += fileConversion.inputFileFormat

    /* Input file */
    //TODO: accepts TOF/TOF too
    //    require(filePath matches """(?i).+\.wiff""", "MS Data Converter only accepts WIFF input files") //move?
    cmdLineBuffer += s""""$inputFilePath"""" //input file path

    /* Output content type */
    val outputContentTypeOpt = paramByName(ParamName.PEAK_PICKING).asInstanceOf[MacroSelectionParam].value
    require(outputContentTypeOpt.isDefined, "outputContentType must be defined")
    cmdLineBuffer += outputContentTypeOpt.get.cmdFlag
    
    /* Output format */
    cmdLineBuffer += fileConversion.outputFileFormat
    /*paramByName(ParamName.PEAK_LIST_FORMAT).asInstanceOf[MacroChoiceParam].value
    require( outputFormatOpt.isDefined, "outputFormat must be defined")
    cmdLineBuffer += outputFormatOpt.get.cmdFlag*/
    
    /* Output file */
    val fileName = new File(inputFilePath).getName
    val outputFormat = DataFileExtension.getPrettyName(fileConversion.outputFileFormat)
    val outputFilePath = fileConversion.outputDirectory + "/" + fileName + "." + outputFormat
    cmdLineBuffer += s""""${outputFilePath}""""

    /* Additional conversion parameters */
    //these are all parameters for mzML only
    if (outputFormat == MZML) {
      for (
        param <- fileConversion.config.params;
        if param.value.isDefined;
        if param.name != ParamName.PEAK_PICKING
      ) {
        param match {
          /** Boolean **/
          case boolean: MacroBooleanParam => {
            if (boolean.value.get == true) cmdLineBuffer += boolean.cmdFlag
          }
          /** Choice **/
          case choice: MacroSelectionParam => {
            cmdLineBuffer += choice.cmdFlag
          }
          case _ => throw new Exception("invalid parameter type for this tool")
        }
      }
    }

    /** Build and return final command string **/
    cmdLineBuffer.mkString(" ")
  }
}