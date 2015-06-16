package fr.profi.msangel.om.workflow.operation.conversion

import java.io.File

import scala.collection.mutable.ArrayBuffer

import fr.profi.msangel.om._
import fr.profi.msangel.om.DataFileExtension
import fr.profi.msangel.om.DataFileExtension._
import fr.profi.msangel.om.workflow.DefaultPeaklistSoftware
import fr.profi.msangel.om.workflow.operation._

object MsConvert extends IFileConversionTool {

  def getName(): FileConversionTool.Value = FileConversionTool.MSCONVERT
  val successExitValue = 0
  val canExecuteProlineParsingRule = true
  val associatedPeaklistSoftware = DefaultPeaklistSoftware.PROTEO_WIZARD_3_0

  
  /**
   * List all output formats handled by MsConvert, linked to their command flag
   */

  private val cmdFlagByOutputFormat = Map[DataFileExtension.Value, String](
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
  
  /**
   * Generate all input/output formats associations handled by MsConvert
   */
  def getFormatMappings(): Array[(DataFileExtension.Value, DataFileExtension.Value)] = cmdFlagByOutputFormat.keySet.map((RAW, _)).toArray

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
    inputFilePath: String,
    conversionToolPath : String,
    fileConversion: FileConversion
  ): String = {

    val cmdLineBuffer = new ArrayBuffer[String]()

    /* Executable **/
    cmdLineBuffer += s""""${conversionToolPath}"""" //msconvert.exe path

    /* RAW input file **/
    require(inputFilePath matches """(?i).+\.raw""", "MSConvert only accepts RAW input files") //move?
    cmdLineBuffer += s""""$inputFilePath"""" //input file path

    /* Output directory and format **/
    cmdLineBuffer += s"""-o "${fileConversion.outputDirectory}""""
    cmdLineBuffer += s"""${_getOutputFormatCmdFlag(fileConversion.outputFileFormat)}"""
    
    /* Force output file name: "inputFilePath.outputFormat" */
    cmdLineBuffer += s"""--outfile "$inputFilePath.${DataFileExtension.getPrettyName(fileConversion.outputFileFormat)}""""

    /* Additional conversion parameters **/
    fileConversion.config.params.withFilter(_.value.isDefined).foreach { param =>
      param match {

        case boolean: MacroBooleanParam => {
          if (boolean.value.get == true) cmdLineBuffer += boolean.cmdFlag
        }
        
        case choice: MacroChoiceParam => {
          cmdLineBuffer += choice.cmdFlag
        }

        case _ => {
          cmdLineBuffer += param.cmdFlag
          cmdLineBuffer += s""""${param.toString()}"""
        }
      }
    }

    // Here is the ExtractMSn TITLE convention
    //cmdLineBuffer += """--filter "titleMaker <RunId>.<ScanNumber>.<ScanNumber>.<ChargeState>.dta" """

    /* Add custom TITLE maker filter using Proline convention **/
    if (fileConversion.useProlineRule) {
      //require(this.canExecuteProlineParsingRule, "Proline parsing rule can't be used with MsConvert.")
      
      val fileName = new File(inputFilePath).getName
      val rawFileIdentifierOpt = if (fileName.endsWith(".raw") || fileName.endsWith(".RAW")) s"raw_file_identifier:${fileName};" else ""

      cmdLineBuffer += s"""--filter "titleMaker first_scan:<ScanNumber>;last_scan:<ScanNumber>;first_time:<ScanStartTimeInMinutes>;last_time:<ScanStartTimeInMinutes>;raw_precursor_moz:<SelectedIonMz>;${rawFileIdentifierOpt}""""
    }

    /* Build and return final command string **/
    cmdLineBuffer.mkString(" ")
  }

  //  /**
  //   * Retrieve output file path from console STDOUT
  //   */
  //  def getOutputFileFromSTDOUT(stdOut: String): Option[String] = {
  //    val pattern = """(?s).*writing output file: (.+mgf).*"""
  //    if (stdOut matches pattern) {
  //      val pattern.r(outputFile) = stdOut
  //      Some(outputFile)
  //    } else None
  //  }

  /**
   *  Get command flag corresponding to output format
   */
  private def _getOutputFormatCmdFlag(format: DataFileExtension.Value): String = cmdFlagByOutputFormat(format)
  
}