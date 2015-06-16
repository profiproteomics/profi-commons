package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.DataFileExtension._
import fr.profi.msangel.om.workflow.DefaultPeaklistSoftware
import fr.profi.msangel.om.workflow.operation._

object MzdbAccess extends IFileConversionTool {

  def getName(): FileConversionTool.Value = FileConversionTool.MZDB_ACCESS
  val successExitValue = 0
  val canExecuteProlineParsingRule = false
  val associatedPeaklistSoftware = DefaultPeaklistSoftware.PROTEO_WIZARD_3_0

  /**
   * Get mzdb-access configuration template
   */
  def getConfigTemplate() = new ConversionToolConfig(
    tool = this.getName()
    //path = "fake/path/mzdb2mgf",
  //TODO : finish (params, filters)
  )
  
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
  def generateCmdLine(filePath: String, conversionToolPath: String, fileConversion: FileConversion): String = ""
}