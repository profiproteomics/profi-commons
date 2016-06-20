package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.DataFileExtension._
import fr.profi.msangel.om.workflow.DefaultPeaklistSoftware
import fr.profi.msangel.om.workflow.operation._
import java.io.File
import scala.collection.mutable.ArrayBuffer

object MzdbAccess extends IFileConversionTool {

  val JAR_NAME = "mzDBaccess.jar"
  val SQLITE4JAVA_DLL_NAME = "sqlite4java-win32-x64.dll"
  val JAVA_LIB_PATH_ARG = "-Djava.library.path="
  
  def getName(): FileConversionTool.Value = FileConversionTool.MZDB_ACCESS
  val successExitValue = 0
  val canExecuteProlineParsingRule = false
  val associatedPeaklistSoftware = Some(DefaultPeaklistSoftware.PROLINE_1_0)

  object ParamName {
    val PRECURSOR_MOZ = "Precursor m/z"
    val PRECURSOR_MOZ_TOL = "Precursor m/z tolerance (PPM)"
    val INTENSITY_CUTOFF = "Intensity cutoff"
  }

  // v 0.9.7
  /*val defaultPrecursorMoz = new MacroChoiceParamItem("Default precursor m/z", "default") //use cmd flag for the value
  val pwizRefinedMoz = new MacroChoiceParamItem("ProteoWizard-refined prec. m/z", "refined_pwiz") //use cmd flag for the value
  val mzdbRefinedMoz = new MacroChoiceParamItem("msDB-refined prec. m/z", "refined_mzdb") //use cmd flag for the value*/

  // v 0.9.8
  val mainPrecursorMoz = new MacroChoiceParamItem("Main precursor m/z", "main_precursor_mz") //default
  val selectedIonMoz = new MacroChoiceParamItem("Selected ion m/z", "selected_ion_mz")
  val refinedMoz = new MacroChoiceParamItem("Refined", "refined")
  val refinedThermoMoz = new MacroChoiceParamItem("Refined (Thermo)", "refined_thermo")

  /**
   * Get mzdb-access configuration template
   */
  def getConfigTemplate() = {

    new ConversionToolConfig(
      tool = this.getName(),
      params = Array(

        // -mzdb: input file path: in generateCmdLine
        // -o: output file path : in generateCmdLine

        MacroSelectionParam(
          name = ParamName.PRECURSOR_MOZ,
          description = Some("Default = " + mainPrecursorMoz.name),
          default = Some(mainPrecursorMoz),
          options = Some(Seq(mainPrecursorMoz, selectedIonMoz, refinedMoz,refinedThermoMoz))
        ),

        MacroNumericParam(
          name = ParamName.PRECURSOR_MOZ_TOL,
          description = Some("m/z tolerance used for precursor m/z value definition, in PPM\nDefault = 20"),
          isRequired = false,
          cmdFlag = "--mz_tol_ppm",
          default = Some(BigDecimal(20))
        ),

        MacroNumericParam(
          name = ParamName.INTENSITY_CUTOFF,
          description = Some("Optional intensity cutoff.\nDefault = 0"),
          isRequired = false,
          cmdFlag = "--intensity_cutoff",
          default = Some(BigDecimal(0))
        ),

        MacroBooleanParam(
          name = "Use Proline convention for title",
          description = Some("Export TITLE using the Proline convention.\nDefault = false"),
          cmdFlag = "--proline_title",
          default = Some(false)
        )
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
          require(selection.name == ParamName.PRECURSOR_MOZ)
          cmdLineBuffer += "-precmz " + selection.cmdFlag
        }

        case numeric: MacroNumericParam => {
          cmdLineBuffer += numeric.cmdFlag + " " + numeric.value.get
        }

        case boolean: MacroBooleanParam => {
          if (boolean.value.get == true) cmdLineBuffer += boolean.cmdFlag
        }

        case _ => throw new Exception("Invalid parameter type for this tool: " + param)
      }
    }


    /* Build and return final command string **/
    cmdLineBuffer.mkString(" ")
  }
}