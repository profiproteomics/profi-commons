package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._

object Raw2mzDB extends IFileConversionTool {

  /**
   * Get raw2mzDB configuration
   */
  def getConfigTemplate() = new ConversionToolConfig(
    tool = FileConversionTool.RAW2MZDB,
    //path = "fake/path/raw2mzdb",
    formatMappings = Array((DataFileFormat.RAW, DataFileFormat.MZDB))
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