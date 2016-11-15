package fr.profi.msangel.om.workflow.operation.conversion

import scala.collection.mutable.ArrayBuffer

import java.io.File

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._
import fr.profi.msangel.om.workflow.operation.ConversionToolConfig
import fr.profi.msangel.om.workflow.operation.IFileConversionTool
import fr.profi.util.scala.BigDecimalRange


object Raw2mzDB extends IFileConversionTool {

  def getName(): FileConversionTool.Value = FileConversionTool.RAW2MZDB
  lazy val supportedVersion = Some("0.9.9")
  lazy val successExitValue = 0
  lazy val canExecuteProlineParsingRule = false
  lazy val associatedPeaklistSoftware = None

  /* Choices for acquisition mode */
  val acquisitionDDA = MacroSelectionParamItem("DDA", "dda")
  val acquisitionDIA = MacroSelectionParamItem("DIA", "dia")
  val acquisitionAuto = MacroSelectionParamItem("Auto", "auto")

  /* Range parameters properties */
  lazy val cyclesRangeParamName = "Only convert the selected range of cycles"
  lazy val cyclesRangeParamDescOpt = Some(
    "Only convert the selected range of cycles.\n" +
    "Note that using this option will disable progress information.\n" +
    "This parameters accepts two types of entry:\n\n" +
    "* Closed interval: fill both fields,\n" +
    "e.g. 1-10 for the first ten cycles.\n\n" +
    "* Interval open on the right: fill only the min. value,\n" +
    "e.g. 10-<empty> to consider from cycle 10 to the end."
  )
  lazy val numOrRangeDescOpt = Some(
    "This parameters accepts two types of entry:\n\n" +
    "* Numeric: fill only the min. value to select a single MS level,\n" +
    "e.g. 1-<empty> fo MS Level 1.\n\n" +
    "* Interval: fill both fields to select a range of MS levels,\n" +
    "e.g. 1-5 for MS level 1 to MS level 5."
  )

  /**
   * Get raw2mzDB configuration
   */
  def getConfigTemplate() = {

    new ConversionToolConfig(
      tool = this.getName,
      toolVersion = this.supportedVersion,
      params = Array(

        MacroRangeParam(
          name = "Profile (MS levels X to Y)",
          description= numOrRangeDescOpt,
          isRequired = false,
          cmdFlag = "-p",
          default = None,
          allowRightMemberEmpty = Some(true),
          maxValue = Some(3)
        ),
        MacroRangeParam(
          name = "Fitted (MS levels X to Y)",
          description = numOrRangeDescOpt,
          isRequired = false,
          cmdFlag = "-f",
          default = Some((Some(BigDecimal(1)), Some(BigDecimal(3)))),
          allowRightMemberEmpty = Some(true),
          maxValue = Some(3)
        ),
        MacroRangeParam(
          name = "Centroidization (MS levels X to Y)",
          description = numOrRangeDescOpt,
          isRequired = false,
          cmdFlag = "-c",
          default = None,
          allowRightMemberEmpty = Some(true),
          maxValue = Some(3)
        ),
        MacroBooleanParam(
          name = "Safe mode (use centroidization if needed)",
          description = Some("Use centroid mode if the requested mode is not available"),
          cmdFlag = "-s",
          default = Some(true)
        ),
        MacroSelectionParam(
          name = "Acquisition mode",
          cmdFlag = "-a",
          default = Some(acquisitionAuto),
          options = Some(Seq(acquisitionDDA, acquisitionDIA, acquisitionAuto))
        ),
        // v 0.9.8
        /*MacroBooleanParam(
          name = "DIA mode",
          cmdFlag = "--dia",
          default = Some(false)
        ),*/
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
        MacroRangeParam(
          name = cyclesRangeParamName,
          description = cyclesRangeParamDescOpt,
          isRequired = false,
          cmdFlag = "--cycles",
          default = None,
          allowRightMemberEmpty = Some(true)
        ),
        // v 0.9.8
        /*
        MacroNumericParam(
          name = "Number of scans to convert (max: #scans in RAW file)",
          isRequired = false,
          cmdFlag = "--nscans"
        )*/
        MacroBooleanParam(
          name = "64 bits conversion of m/z and intensities (larger output file)",
          cmdFlag = "--no_loss",
          default = Some(false)
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
  def generateCmdLine(
    inputFilePath: String,
    outputFilePath: String,    
    conversionToolPath: String,
    fileConversion: FileConversion,
    javaArgs:Array[String] = Array()
  ): String = {

    val cmdLineBuffer = new ArrayBuffer[String]()

    val inputFileName = new File(inputFilePath).getName

    /* Executable **/
    cmdLineBuffer += s""""${conversionToolPath}"""" //raw2mzdb.exe path

    /* Input file (D, RAW, WIFF) **/
    cmdLineBuffer += s"""-i "$inputFilePath""""

    /* Output file (MZDB) **/
    cmdLineBuffer += s"""-o "$outputFilePath""""

    /* Additional conversion parameters **/
    fileConversion.config.params.withFilter(_.value.isDefined).foreach { param =>
      param match {

        case boolean: MacroBooleanParam => {
          if (boolean.value.get == true) cmdLineBuffer += boolean.cmdFlag
        }

        case numeric: MacroNumericParam => {
          cmdLineBuffer += numeric.cmdFlag
          cmdLineBuffer += numeric.value.get.toString
        }

        case range: MacroRangeParam => {
          val (minOpt, maxOpt) = range.value.get
          cmdLineBuffer += range.cmdFlag

          // Cycles: closed or open interval
          if (range.name == cyclesRangeParamName) {
            cmdLineBuffer += minOpt.getOrElse("").toString + '-' + maxOpt.getOrElse("").toString
          }

          // Profile, fitted, centroided: single number or closed interval
          else {
            if (minOpt.isDefined && maxOpt.isEmpty) cmdLineBuffer += minOpt.get.toString
            else cmdLineBuffer += minOpt.getOrElse("").toString + '-' + maxOpt.getOrElse("").toString
          }
        }

        case selection: MacroSelectionParam => {
          cmdLineBuffer += selection.cmdFlag + ' ' + selection.value.get.value
        }

        case _ => {
          cmdLineBuffer += param.cmdFlag
          cmdLineBuffer += s""""${param.toString()}""""
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