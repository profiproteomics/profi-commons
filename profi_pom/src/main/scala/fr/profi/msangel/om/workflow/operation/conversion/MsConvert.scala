package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._
import fr.profi.msangel.om.DataFileFormat
import scala.collection.mutable.ArrayBuffer
import java.io.File

object MsConvert extends IFileConversionTool {
  
  def getName(): FileConversionTool.Value = FileConversionTool.MSCONVERT

  /**
   * List all output formats handled by MsConvert, linked to their command flag
   */
  import DataFileFormat._

  private val cmdFlagByOutputFormat = Map[DataFileFormat.Value, String](
    MZML -> "--mzML",
    MZXML -> "--mzXML",
    MZ5 -> "--mz5",
    MGF -> "--mgf",
    TEXT -> "--text",
    MS1 -> "--ms1",
    CMS1 -> "--cms1",
    MS2 -> "--ms2",
    CMS2 -> "--cms2"
  )

  /**
   * Get MsConvert default configuration template
   */
  def getConfigTemplate(): ConversionToolConfig = {

    val bits64 = new MacroChoiceParamItem("64 bits", "--64")
    val bits32 = new MacroChoiceParamItem("32 bits", "--32")
    val mz64 = new MacroChoiceParamItem("64 bits", "--mz64")
    val mz32 = new MacroChoiceParamItem("32 bits", "--mz32")
    val i64 = new MacroChoiceParamItem("64 bits", "--inten64")
    val i32 = new MacroChoiceParamItem("32 bits", "--inten32")

    new ConversionToolConfig(
      tool = this.getName(),
      //path = """C:\Program Files\ProteoWizard 3.0.7076\msconvert.exe""", //TODO : move, get from GUI

      params = Array(
        //MacroStringParam(name = "Output format", isRequired = true, cmdFlag = ""),
        //MacroStringParam(name = "Extension", isRequired = false, cmdFlag = ""),

        MacroChoiceParam(name = "Binary encoding precision", default = Some(bits64), options = Some(Seq(bits64, bits32))),
        MacroChoiceParam(name = "Binary encoding precision on m/z", default = Some(mz64), options = Some(Seq(mz64, mz32))), //mzML only
        MacroChoiceParam(name = "Binary encoding precision on intensity", default = Some(i32), options = Some(Seq(i64, i32))), //mzML only

        MacroBooleanParam(name = "Omit index (mzML)", cmdFlag = "--noindex", default = Some(false)),
        MacroBooleanParam(name = "Use zlib compression", cmdFlag = "--zlib", default = Some(true)),
        MacroBooleanParam(name = "Package in gzip", cmdFlag = "--gzip", default = Some(false)),
        MacroBooleanParam(name = "TPP compatibility", cmdFlag = "", default = Some(false)),

        MacroBooleanParam(name = "Merge MS/MS", cmdFlag = "--merge", default = Some(false)),
        MacroBooleanParam(name = "Write ion monitoring as spectra", cmdFlag = "--simAsSpectra", default = Some(false)),
        MacroBooleanParam(name = "Write reaction monitoring as spectra", cmdFlag = "--srmAsSpectra", default = Some(false))
      )
      /*,
      filters = Array(
      )
      */
    )

  }
  
  def getFormatMappings(): Array[(DataFileFormat.Value, DataFileFormat.Value)] = {
    cmdFlagByOutputFormat.keySet.map((RAW, _)).toArray
  }

  /**
   *  Check tool path conformity
   */
  def checkExePath(conversionToolPath: String): Boolean = {

    val containsExe = conversionToolPath.endsWith("msconvert.exe") //? can be renamed...
    val exeExistsAtPath = new File(conversionToolPath).exists()
    //    println("containsExe: " + containsExe)
    //    println("exeAtPath: " + exeAtPath)

    if (containsExe && exeExistsAtPath) true else false
    //    require(containsExe, "Exe path must finish by msconvert.exe") //? can be renamed...
    //    require(exeAtPath, "msconvert.exe can't be found at specified path")
  }

  /**
   *  Command line generator
   */
  def generateCmdLine(
    filePath: String,
    conversionToolPath : String,
    fileConversion: FileConversion
  ): String = {

    val cmdLineBuffer = new ArrayBuffer[String]()

    /** Executable **/
    cmdLineBuffer += s""""${conversionToolPath}"""" //msconvert.exe path

    /** RAW input file **/
    require(filePath matches """(?i).+\.raw""", "MSConvert only accepts RAW input files") //move?
    cmdLineBuffer += s""""$filePath"""" //input file path

    /** Output directory and format **/
    cmdLineBuffer += s"""-o "${fileConversion.outputDirectory}""""
    cmdLineBuffer += s""""${_getOutputFormatCmdFlag(fileConversion.outputFileFormat)}""""

    /** Additional conversion parameters **/
    fileConversion.config.params.withFilter(_.value.isDefined).foreach { param =>
      param match {

        /** Boolean **/
        case boolean: MacroBooleanParam => {
          if (boolean.value.get == true) cmdLineBuffer += boolean.cmdFlag
        }

        /** Choice **/
        case choice: MacroChoiceParam => {
          cmdLineBuffer += choice.value.get.cmdFlag
        }

        /** Other **/
        case _ => {
          cmdLineBuffer += param.cmdFlag
          cmdLineBuffer += s""""${param.toString()}"""
        }
      }
    }
    
    // Here is the ExtractMSn TITLE convention
    //cmdLineBuffer += """--filter "titleMaker <RunId>.<ScanNumber>.<ScanNumber>.<ChargeState>.dta" """
    
    /** Add custom TITLE maker filter using unknown convention **/
    cmdLineBuffer += """--filter "titleMaker File:\"<RunId>\", scan=<ScanNumber>" """ // File:"OE.raw", scan=1
    // TODO: check that apply_spec_title_parsing_rule WS returns something like :
    // {"last_scan":"1","first_scan":"1","raw_file_name":"OE.raw"}
    // WS URL = http://hostname:8080/proline/admin/util/apply_spec_title_parsing_rule
    // WS request body = { "rule_id": 10, "spectrum_title": "File:\"OE.raw\", scan=1" }

    /** Build and return final command string **/
    cmdLineBuffer.mkString(" ")
  }

  /**
   * Retrieve output file path from console STDOUT
   */
  def getOutputFileFromSTDOUT(stdOut: String): Option[String] = {
    val pattern = """(?s).*writing output file: (.+mgf).*"""
    if (stdOut matches pattern) {
      val pattern.r(outputFile) = stdOut
      Some(outputFile)
    } else None
  }

  
  /** 
   *  Get command flag corresponding to output format
   */
  private def _getOutputFormatCmdFlag(format: DataFileFormat.Value): String = cmdFlagByOutputFormat(format)
  
}