package fr.profi.msangel.om.workflow.operation.conversion

import fr.profi.msangel.om._
import fr.profi.msangel.om.workflow.operation._
import scala.collection.mutable.ArrayBuffer

object ExtractMSn extends IFileConversionTool {

  /**
   * Get ExtractMSn configuration template
   */
  def getConfigTemplate() = {

    val precCharges = Seq("All") ++ (1 to 8).map(n => s"$n+ only")

    new ConversionToolConfig(
      tool = FileConversionTool.EXTRACT_MSN,
      //path = "fake/path/raw2mgf",
      formatMappings = Array((DataFileFormat.RAW, DataFileFormat.MGF)),

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