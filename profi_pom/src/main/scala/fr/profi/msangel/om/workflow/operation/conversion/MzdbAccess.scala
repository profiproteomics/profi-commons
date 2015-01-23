package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._

object MzdbAccess extends IFileConversionTool {
  
  /**
   * Get mzdb-access configuration template
   */
  def getConfigTemplate()  = new ConversionToolConfig(
    tool = FileConversionTool.MZDB_ACCESS,
    //path = "fake/path/mzdb2mgf",
    formatMappings = Array((DataFileFormat.MZDB, DataFileFormat.MGF))
  //TODO : finish (params, filters)
  )

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