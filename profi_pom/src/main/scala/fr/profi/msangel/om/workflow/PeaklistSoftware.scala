package fr.profi.msangel.om.workflow


/** 
 *  Model for PeaklistSoftware 
 **/
//TODO: move to right place
case class PeaklistSoftware(
  name: String,
  id: Option[Long] = None,
  versionOpt: Option[String] = None
) {
  require(if (id.isDefined) id.get > 0 else true, "Invalid id for peaklist software")
  require(name != null, "peaklist software name is null")

  override def toString() = s"${this.name} ${this.versionOpt.getOrElse("")}"
  
  def matches(pkSoft: PeaklistSoftware): Boolean = {
    if (pkSoft.name == this.name) {
      pkSoft.getCorrectVersionOpt() == this.getCorrectVersionOpt()
      //FIXME: Registering new peaklist soft. through PWeb will generate Some("") if no version is provided 
    } else false
  }

  // TODO: delete me when obsolete
  def getCorrectVersionOpt(): Option[String] = {
    versionOpt.map{ version =>
      if (version.isEmpty()) null else version
    }
  }
}

/**
 * Enumerate all peaklist software implemented in Proline
 **/
//TODO: move to right place
object DefaultPeaklistSoftware { //TODO: rename, move...
  val EXTRACT_MSN = PeaklistSoftware(name = "extract_msn.exe", versionOpt = None)
  val DATA_ANALYSIS_4_0 = PeaklistSoftware(name = "Data Analysis", versionOpt = Some("4.0"))
  val DATA_ANALYSIS_4_1 = PeaklistSoftware(name = "Data Analysis", versionOpt = Some("4.1"))
  val MASCOT_DLL = PeaklistSoftware(name = "mascot.dll", versionOpt = None)
  val MASCOT_DISTILLER = PeaklistSoftware(name = "Mascot Distiller", versionOpt = None)
  val MAXQUANT = PeaklistSoftware(name = "MaxQuant", versionOpt = None)
  val PROLINE_1_0 = PeaklistSoftware(name = "Proline", versionOpt = Some("1.0"))
  val PROTEIN_PILOT = PeaklistSoftware(name = "Protein Pilot", versionOpt = None)
  val PROTEOME_DISCOVERER = PeaklistSoftware(name = "Proteome Discoverer", versionOpt = None)
  val PROTEO_WIZARD_2_0 = PeaklistSoftware(name = "ProteoWizard", versionOpt = Some("2.0"))
  val PROTEO_WIZARD_2_1 = PeaklistSoftware(name = "ProteoWizard", versionOpt = Some("2.1"))
  val PROTEO_WIZARD_3_0 = PeaklistSoftware(name = "ProteoWizard", versionOpt = Some("3.0"))
  val SPECTRUM_MILL = PeaklistSoftware(name = "Spectrum Mill", versionOpt = None)
}