package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._

object MzdbAccess extends IFileConversionTool {
  
  def getName(): FileConversionTool.Value = FileConversionTool.MZDB_ACCESS
  
  /**
   * Get mzdb-access configuration template
   */
  def getConfigTemplate() = new ConversionToolConfig(
    tool = this.getName()
    //path = "fake/path/mzdb2mgf",
  //TODO : finish (params, filters)
  )
  
  def getFormatMappings(): Array[(DataFileFormat.Value, DataFileFormat.Value)] = {
    Array((DataFileFormat.MZDB, DataFileFormat.MGF))
  }

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