package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._

object Raw2mzDB extends IFileConversionTool {
  
  def getName(): FileConversionTool.Value = FileConversionTool.RAW2MZDB

  /**
   * Get raw2mzDB configuration
   */
  def getConfigTemplate() = new ConversionToolConfig(
    tool = this.getName
    //path = "fake/path/raw2mzdb",
  //TODO : finish (params, filters)
  )
  
  def getFormatMappings(): Array[(DataFileFormat.Value, DataFileFormat.Value)] = {
    Array((DataFileFormat.RAW, DataFileFormat.MZDB))
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