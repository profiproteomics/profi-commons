package fr.profi.msangel.om.workflow.operation.conversion

import scala.collection.mutable.ArrayBuffer

import java.io.File

import fr.profi.msangel.om._
import fr.profi.msangel.om.DataFileExtension._
import fr.profi.msangel.om.workflow.DefaultPeaklistSoftware
import fr.profi.msangel.om.workflow.operation._

object MzdbAccess extends IFileConversionTool {

  val JAR_NAME = "mzDBaccess.jar"
  val SQLITE4JAVA_DLL_NAME = "sqlite4java-win32-x64.dll"
  val JAVA_LIB_PATH_ARG = "-Djava.library.path="

  def getName(): FileConversionTool.Value = FileConversionTool.MZDB_ACCESS
  lazy val supportedVersion = Some("0.7")
  lazy val successExitValue = 0
  lazy val canExecuteProlineParsingRule = true
  lazy val associatedPeaklistSoftware = Some(DefaultPeaklistSoftware.PROLINE_1_0)

  // mzDB v0.9.7
  /*val defaultPrecursorMoz = new MacroSelectionParamItem("Default precursor m/z", "default") //use cmd flag for the value
  val pwizRefinedMoz = new MacroSelectionParamItem("ProteoWizard-refined prec. m/z", "refined_pwiz") //use cmd flag for the value
  val mzdbRefinedMoz = new MacroSelectionParamItem("msDB-refined prec. m/z", "refined_mzdb") //use cmd flag for the value*/

  // v0.5.0, mzDB v0.9.8
  val mainPrecursorMoz = new MacroSelectionParamItem("Main precursor m/z", "main_precursor_mz") //default
  val selectedIonMoz = new MacroSelectionParamItem("Selected ion m/z", "selected_ion_mz")
  val refinedMoz = new MacroSelectionParamItem("Refined", "refined")
  val refinedThermoMoz = new MacroSelectionParamItem("Refined (Thermo)", "refined_thermo")

  /**
   * Get mzdb-access configuration template
   */
  def getConfigTemplate() = {

    new ConversionToolConfig(
      tool = this.getName(),
      toolVersion = this.supportedVersion,

      params = Array(

        // -mzdb: input file path: in generateCmdLine
        // -o: output file path : in generateCmdLine

        MacroSelectionParam(
          name = "Precursor m/z",
          cmdFlag = "-precmz",
          description = Some("Default = " + mainPrecursorMoz.name),
          default = Some(refinedThermoMoz),
          options = Some(Seq(mainPrecursorMoz, selectedIonMoz, refinedMoz,refinedThermoMoz))
        ),

        MacroNumericParam(
          name = "Precursor m/z tolerance (ppm)",
          description = Some("m/z tolerance used for precursor m/z value definition, in ppm\nDefault = 20"),
          isRequired = false,
          cmdFlag = "--mz_tol_ppm",
          default = Some(BigDecimal(20))
        ),

        MacroNumericParam(
          name = "Intensity cutoff",
          description = Some("Optional intensity cutoff.\nDefault = 0"),
          isRequired = false,
          cmdFlag = "--intensity_cutoff",
          default = Some(BigDecimal(0))
        ),

        MacroNumericParam(
          name = "MS level to export",
          description = Some("The MS level to export.\nDefault = 2"),
          isRequired = false,
          cmdFlag = "--ms_level",
          default = Some(BigDecimal(2))
        )

        // Handled by custom MS-Angel GUI parameter "Use Proline parsing rule"
        /*MacroBooleanParam(
          name = "Use Proline convention for title",
          description = Some("Export TITLE using the Proline convention.\nDefault = false"),
          cmdFlag = "--proline_title",
          default = Some(false)
        )*/
      )
    )
  }

  /**
   * Generate all input/output formats associations handled by MsConvert
   */
  def getFormatMappings(): Array[(DataFileExtension.Value, DataFileExtension.Value)] = Array(
    (DataFileExtension.MZDB, DataFileExtension.MGF)
  )

  /**
   *  Check tool path conformity
   */
  def checkExePath(conversionToolPath: String): Boolean = {
    val containsExe = conversionToolPath.endsWith(JAR_NAME)
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
    javaArgs: Array[String]
  ): String = {

    //java -Xmx1024M -jar mzDBaccess.jar create_mgf -mzdb "TT66AJS150416_06.wiff.mzDB" -o "TT66AJS150416_06.wiff.mzDB.mgf" 
    //-ptitle
    //-precmz refined_pwiz
    //-cutoff 3

    val cmdLineBuffer = new ArrayBuffer[String]()
    

    val inputFileName = new File(inputFilePath).getName
    val outputFormat = DataFileExtension.getPrettyName(fileConversion.outputFileFormat)

    /* Call java */
    cmdLineBuffer += "java"
    
    /* Java options */
    // Include reference folder where the sqlite4java dll can be found
    val someRefToSqlite4javaDll = javaArgs.find ( _ matches """^-Djava\.library\.path=.+$""" ).isDefined
    require(someRefToSqlite4javaDll, s"The reference to '$SQLITE4JAVA_DLL_NAME' folder is required to use mzdbAccess.\nPlease check your configuration.")
    
    javaArgs.foreach( cmdLineBuffer += _ )
    
    /* Launch jar with option 'create_mgf' **/
    //TODO: add java options
    cmdLineBuffer += s"""-jar "$conversionToolPath" create_mgf""" //raw2mzdb.exe path

    /* Input file (mzDB) **/
    cmdLineBuffer += s"""-mzdb "$inputFilePath""""

    /* Output file (MGF) **/
    cmdLineBuffer += s"""-o "$outputFilePath""""

    /* Additional conversion parameters **/
    fileConversion.config.params.withFilter(_.value.isDefined).foreach { param =>
      param match {

        case selection: MacroSelectionParam => {
          cmdLineBuffer += selection.cmdFlag + ' ' + selection.value.get.value
        }

        case numeric: MacroNumericParam => {
          cmdLineBuffer += numeric.cmdFlag + " " + numeric.value.get
        }

        case _ => throw new Exception("Invalid parameter type for this tool: " + param)
      }
    }
    
    /* Proline title */
    if (fileConversion.useProlineRule) {
      cmdLineBuffer += "-ptitle"
    }

    /* Build and return final command string **/
    cmdLineBuffer.mkString(" ")
  }
}