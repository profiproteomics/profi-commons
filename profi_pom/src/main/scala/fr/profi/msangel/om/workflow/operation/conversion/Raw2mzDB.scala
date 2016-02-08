package fr.profi.msangel.om.workflow.operation.conversion

import java.io.File
import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.DefaultPeaklistSoftware
import fr.profi.msangel.om.workflow.operation._
import fr.profi.msangel.om.workflow.operation.ConversionToolConfig
import fr.profi.msangel.om.workflow.operation.IFileConversionTool
import fr.profi.util.scala.BigDecimalRange
import scala.collection.mutable.ArrayBuffer


object Raw2mzDB extends IFileConversionTool {

  def getName(): FileConversionTool.Value = FileConversionTool.RAW2MZDB
  val successExitValue = 0
  val canExecuteProlineParsingRule = false
  val associatedPeaklistSoftware = None

  /**
   * Get raw2mzDB configuration
   */
  def getConfigTemplate() = {

    val profileModeParam = MacroRangeParam(
      name = "Profile (MS levels X to Y)",
      isRequired = false,
      cmdFlag = "-p",
      default = None
    )

    val fittedMsLevelsRange: BigDecimalRange = (Some(BigDecimal(1)), Some(BigDecimal(3)))
    val fittedModeParam = MacroRangeParam(
      name = "Fitted (MS levels X to Y)",
      isRequired = false,
      cmdFlag = "-f",
      default = Some(fittedMsLevelsRange)
    )
    val centroidModeParam = MacroRangeParam(
      name = "Centroidization (MS levels X to Y)",
      isRequired = false,
      cmdFlag = "-c",
      default = None
    )

    new ConversionToolConfig(
      tool = this.getName,
      params = Array(
        profileModeParam,
        fittedModeParam,
        centroidModeParam,
        MacroNumericParam(
          name = "Bounding box time width for MS1 (seconds)",
          isRequired = false,
          cmdFlag = "-T",
          default = Some(BigDecimal(15))
        ),
        MacroNumericParam(
          name = "Bounding box m/z width for MS1 (Da)",
          isRequired = false,
          cmdFlag = "-M",
          default = Some(BigDecimal(5))
        ),
        MacroNumericParam(
          name = "Bounding box time width for MSn (seconds)",
          isRequired = false,
          cmdFlag = "-t",
          default = Some(BigDecimal(0))
        ),
        MacroNumericParam(
          name = "Bounding box m/z width for MSn (Da)",
          isRequired = false,
          cmdFlag = "-m",
          default = Some(BigDecimal(10000))
        ),
        MacroBooleanParam(
          name = "DIA mode",
          cmdFlag = "--dia",
          default = Some(false)
        ),
        MacroBooleanParam(
          name = "64 bits conversion of m/z and intensities (larger output file)",
          cmdFlag = "--no_loss",
          default = Some(false)
        ),
        MacroNumericParam(
          name = "Number of scans to convert (max: #scans in RAW file)",
          isRequired = false,
          cmdFlag = "--nscans"
        )
      )
    )
  }

  /**
   * Generate all input/output formats associations handled by MsConvert
   */
  def getFormatMappings(): Array[(DataFileExtension.Value, DataFileExtension.Value)] = Array(
    (DataFileExtension.D, DataFileExtension.MZDB),
    (DataFileExtension.RAW, DataFileExtension.MZDB),
    (DataFileExtension.WIFF, DataFileExtension.MZDB)
  )

  /**
   *  Check tool path conformity
   */
  def checkExePath(conversionToolPath: String): Boolean = {
    val containsExe = conversionToolPath.endsWith("raw2mzDB.exe")
    val exeExistsAtPath = new File(conversionToolPath).exists()
    containsExe && exeExistsAtPath
  }

  /**
   *  Generate command line
   */
  //TODO
  def generateCmdLine(
    inputFilePath: String,
    outputFilePath: String,    
    conversionToolPath: String,
    fileConversion: FileConversion
  ): String = {

    val cmdLineBuffer = new ArrayBuffer[String]()

    val inputFileName = new File(inputFilePath).getName

    /* Executable **/
    cmdLineBuffer += s""""${conversionToolPath}"""" //raw2mzdb.exe path

    /* Input file (D, RAW, WIFF) **/
    cmdLineBuffer += s"""-i "$inputFilePath""""

    /* Output file (MZDB) **/
    val fileName = new File(inputFilePath).getName
    val outputFormat = DataFileExtension.getPrettyName(fileConversion.outputFileFormat)
//    val outputFilePath = fileConversion.outputDirectory + "/" + fileName + "." + outputFormat
    cmdLineBuffer += s"""-o "$outputFilePath""""

    /* Additional conversion parameters **/
    fileConversion.config.params.withFilter(_.value.isDefined).foreach { param =>
      param match {

        case boolean: MacroBooleanParam => {
          if (boolean.value.get == true) cmdLineBuffer += boolean.cmdFlag
        }
        
        case numeric: MacroNumericParam => {
          cmdLineBuffer += numeric.cmdFlag +" " + numeric.value.get
        }
        case range: MacroRangeParam => {
          val bigDecimalRange = range.value.get
          cmdLineBuffer += range.cmdFlag +" "+ bigDecimalRange._1.get + "-" + bigDecimalRange._2.get
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

      val splittedInputFileName = inputFileName.split("""\.""")
      //require(splittedInputFileName.length == 2, "incorrect input file name: "+new File(inputFilePath).getName)
      val rawFileIdentifier = s"raw_file_identifier:${splittedInputFileName.head};"

      cmdLineBuffer += s"""--filter "titleMaker first_scan:<ScanNumber>;last_scan:<ScanNumber>;first_time:<ScanStartTimeInMinutes>;last_time:<ScanStartTimeInMinutes>;raw_precursor_moz:<SelectedIonMz>;${rawFileIdentifier}""""
    }

    /* Build and return final command string **/
    cmdLineBuffer.mkString(" ")
  }
}