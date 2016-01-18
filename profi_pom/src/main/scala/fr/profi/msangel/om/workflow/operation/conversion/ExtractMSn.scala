package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.DefaultPeaklistSoftware
import fr.profi.msangel.om.workflow.operation._

object ExtractMSn extends IFileConversionTool {

  def getName(): FileConversionTool.Value = FileConversionTool.EXTRACT_MSN
  val successExitValue = 0
  val canExecuteProlineParsingRule = false
  val associatedPeaklistSoftware = Some(DefaultPeaklistSoftware.EXTRACT_MSN)

  /**
   * Get ExtractMSn configuration template
   */
  def getConfigTemplate() = {

    val precCharges = Seq("All") ++ (1 to 8).map(n => s"$n+ only")

    new ConversionToolConfig(
      tool = this.getName(),
      //path = "fake/path/raw2mgf",

      params = Array(
        MacroNumericParam(name = "First scan", isRequired = false, cmdFlag = ""),
        MacroNumericParam(name = "Last scan", isRequired = false, cmdFlag = ""),
        MacroNumericParam(name = "Min. precursor mass", isRequired = true, default = Some(600), cmdFlag = ""),
        MacroNumericParam(name = "Max. precursor mass", isRequired = true, default = Some(3500), cmdFlag = ""),
        MacroNumericParam(name = "Grouping tolerance", isRequired = true, default = Some(1), cmdFlag = ""),
        MacroNumericParam(name = "Intermediate scans", isRequired = true, default = Some(0), cmdFlag = ""),
        MacroStringParam(name = "Precursor charge", isRequired = true, default = Some("All"), options = Some(precCharges), cmdFlag = ""),
        MacroNumericParam(name = "Min. scans/group", isRequired = true, default = Some(1), cmdFlag = ""),
        MacroNumericParam(name = "Min. peaks", isRequired = true, default = Some(10), cmdFlag = ""),
        MacroNumericParam(name = "Min. intensity", isRequired = false, cmdFlag = ""),
        MacroNumericParam(name = "Min. S/N", isRequired = true, default = Some(3), cmdFlag = ""),
        MacroNumericParam(name = "Min. major peaks", isRequired = true, default = Some(5), cmdFlag = "")
      )
    )
  }

  /**
   * Generate all input/output formats associations handled by MsConvert
   */
  def getFormatMappings(): Array[(DataFileExtension.Value, DataFileExtension.Value)] = Array()

  /**
   *  Check tool path conformity
   */
  def checkExePath(conversionToolPath: String): Boolean = {
    //TODO
    false
  }

  /**
   *  Generate command line
   */
  //TODO
  def generateCmdLine(inputFilePath: String, conversionToolPath: String, fileConversion: FileConversion): String = ""

}