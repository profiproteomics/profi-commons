package fr.profi.msangel.om.sel

import javax.xml.bind.annotation._

// container classes for omssa settings
@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_precursorsearchtype(MSSearchType: Int)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_productsearchtype(MSSearchType: Int)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_ionstosearch(MSIonType: Array[Int])

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_zdep(MSZdependence: Int)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_fixed(MSMod: Array[Int])

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_variable(MSMod: Array[Int])

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_enzyme(MSEnzymes: Int)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_chargehandling(MSChargeHandle: MSChargeHandle)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSChargeHandle() {
  var MSChargeHandle_calcplusone: MSChargeHandle_calcplusone = new MSChargeHandle_calcplusone(OmssaLists.calcPlusOne(""))
  var MSChargeHandle_calccharge: MSChargeHandle_calccharge = new MSChargeHandle_calccharge(OmssaLists.calcCharge(""))
  var MSChargeHandle_mincharge: Int = 1
  var MSChargeHandle_maxcharge: Int = 4
  var MSChargeHandle_considermult: Int = 3
  var MSChargeHandle_plusone: Double = 0.95
  var MSChargeHandle_maxproductcharge: Int = 3
  var MSChargeHandle_negative: Int = 1
}

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSChargeHandle_calcplusone(MSCalcPlusOne: Int)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSChargeHandle_calccharge(MSCalcCharge: Int)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_iterativesettings(MSIterativeSettings: MSIterativeSettings)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSIterativeSettings() {
  var MSIterativeSettings_researchthresh: Double = 0.01
  var MSIterativeSettings_subsetthresh: Double = 0
  var MSIterativeSettings_replacethresh: Double = 0
}

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_infiles(MSInFile: MSInFile)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSInFile(MSInFile_infile: String, MSInFile_infiletype: MSInFile_infiletype)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSInFile_infiletype(MSSpectrumFileType: Int)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings_outfiles(MSOutFile: MSOutFile)

@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSOutFile(MSOutFile_outfile: String, MSOutFile_outfiletype: MSOutFile_outfiletype, MSOutFile_includerequest: MSOutFile_includerequest) {
  if (!MSOutFile_outfile.endsWith(".omx.bz2")) {
    MSOutFile_outfile + ".omx.bz2"
  }
}
@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSOutFile_outfiletype(MSSerialDataFormat: Int)

// classes for attributes
case class MSSearchSettings_pepppm(pepToleranceUnitInPpm: Boolean) {
  @XmlAttribute private val value: Boolean = pepToleranceUnitInPpm
}

case class MSSearchSettings_msmsppm(msmsToleranceUnitInPpm: Boolean) {
  @XmlAttribute private val value: Boolean = msmsToleranceUnitInPpm
}

case class MSSearchSettings_nmethionine(nmethionine: Boolean) {
  @XmlAttribute private val value: Boolean = nmethionine
}

case class MSOutFile_includerequest(includeRequest: Boolean) {
  @XmlAttribute private val value: Boolean = includeRequest
}

// main root element
// using inside variable instead of class arguments because they are limited to 22 in Scala 2.10
@XmlRootElement(name = "MSSearchSettings")
@XmlAccessorType(value = XmlAccessType.FIELD)
case class MSSearchSettings() {

  @XmlElement(name = "MSSearchSettings_precursorsearchtype")
  // TODO: rename to precursorSearchType, remove private and remove set method
  private var MSSearchSettings_precursorsearchtype: MSSearchSettings_precursorsearchtype = new MSSearchSettings_precursorsearchtype(OmssaLists.searchTypes(""))
  // TODO: rename to setprecursorSearchTypeFromString
  def setMSSearchSettings_precursorsearchtype(value: Option[String]) { if (value.isDefined) MSSearchSettings_precursorsearchtype = new MSSearchSettings_precursorsearchtype(OmssaLists.searchTypes(value.get.toLowerCase())) }

  private var MSSearchSettings_productsearchtype: MSSearchSettings_productsearchtype = new MSSearchSettings_productsearchtype(OmssaLists.searchTypes(""))
  def setMSSearchSettings_productsearchtype(value: Option[String]) { if (value.isDefined) MSSearchSettings_productsearchtype = new MSSearchSettings_productsearchtype(OmssaLists.searchTypes(value.get.toLowerCase())) }

  private var MSSearchSettings_ionstosearch: MSSearchSettings_ionstosearch = new MSSearchSettings_ionstosearch(Array(OmssaLists.ionTypes("b"), OmssaLists.ionTypes("y")))
  def setMSSearchSettings_ionstosearch(values: Option[Array[String]]) { if (values.isDefined) MSSearchSettings_ionstosearch = new MSSearchSettings_ionstosearch(values.get.map(i => OmssaLists.ionTypes(i.toLowerCase()))) }

  private var MSSearchSettings_peptol: Double = 0.25
  def setMSSearchSettings_peptol(value: Option[Double]) { if (value.isDefined) MSSearchSettings_peptol = value.get }

  private var MSSearchSettings_msmstol: Double = 0.25
  def setMSSearchSettings_msmstol(value: Option[Double]) { if (value.isDefined) MSSearchSettings_msmstol = value.get }

  private var MSSearchSettings_zdep: MSSearchSettings_zdep = new MSSearchSettings_zdep(0)
  def setMSSearchSettings_zdep(value: Option[Int]) { if (value.isDefined) MSSearchSettings_zdep = new MSSearchSettings_zdep(value.get) }

  private var MSSearchSettings_cutoff: Double = 10
  def setMSSearchSettings_cutoff(value: Option[Double]) { if (value.isDefined) MSSearchSettings_cutoff = value.get }

  private var MSSearchSettings_cutlo: Double = 0
  def setMSSearchSettings_cutlo(value: Option[Double]) { if (value.isDefined) MSSearchSettings_cutlo = value.get }

  private var MSSearchSettings_cuthi: Double = 0.2
  def setMSSearchSettings_cuthi(value: Option[Double]) { if (value.isDefined) MSSearchSettings_cuthi = value.get }

  private var MSSearchSettings_cutinc: Double = 0.0005
  def setMSSearchSettings_cutinc(value: Option[Double]) { if (value.isDefined) MSSearchSettings_cutinc = value.get }

  private var MSSearchSettings_singlewin: Int = 20
  def setMSSearchSettings_singlewin(value: Option[Int]) { if (value.isDefined) MSSearchSettings_singlewin = value.get }

  private var MSSearchSettings_doublewin: Int = 14
  def setMSSearchSettings_doublewin(value: Option[Int]) { if (value.isDefined) MSSearchSettings_doublewin = value.get }

  private var MSSearchSettings_singlenum: Int = 2
  def setMSSearchSettings_singlenum(value: Option[Int]) { if (value.isDefined) MSSearchSettings_singlenum = value.get }

  private var MSSearchSettings_doublenum: Int = 2
  def setMSSearchSettings_doublenum(value: Option[Int]) { if (value.isDefined) MSSearchSettings_doublenum = value.get }

  private var MSSearchSettings_fixed: MSSearchSettings_fixed = new MSSearchSettings_fixed(Array.empty)
  def setMSSearchSettings_fixed(value: Option[Array[Int]]) { if (value.isDefined) MSSearchSettings_fixed = new MSSearchSettings_fixed(value.get) }

  private var MSSearchSettings_variable: MSSearchSettings_variable = new MSSearchSettings_variable(Array.empty)
  def setMSSearchSettings_variable(value: Option[Array[Int]]) { if (value.isDefined) MSSearchSettings_variable = new MSSearchSettings_variable(value.get) }

  private var MSSearchSettings_enzyme: MSSearchSettings_enzyme = new MSSearchSettings_enzyme(OmssaLists.enzymes(""))
  def setMSSearchSettings_enzyme(value: Option[String]) { if (value.isDefined) MSSearchSettings_enzyme = new MSSearchSettings_enzyme(OmssaLists.enzymes(value.get.toLowerCase())) }

  private var MSSearchSettings_missedcleave: Int = 0
  def setMSSearchSettings_missedcleave(value: Option[Int]) { if (value.isDefined) MSSearchSettings_missedcleave = value.get }

  private var MSSearchSettings_hitlistlen: Int = 25
  def setMSSearchSettings_hitlistlen(value: Option[Int]) { if (value.isDefined) MSSearchSettings_hitlistlen = value.get }

  private var MSSearchSettings_db: String = ""
  def setMSSearchSettings_db(value: Option[String]) { if (value.isDefined) MSSearchSettings_db = value.get.replaceAll("\\\\", "/") }

  private var MSSearchSettings_tophitnum: Int = 6
  def setMSSearchSettings_tophitnum(value: Option[Int]) { if (value.isDefined) MSSearchSettings_tophitnum = value.get }

  private var MSSearchSettings_minhit: Int = 2
  def setMSSearchSettings_minhit(value: Option[Int]) { if (value.isDefined) MSSearchSettings_minhit = value.get }

  private var MSSearchSettings_minspectra: Int = 4
  def setMSSearchSettings_minspectra(value: Option[Int]) { if (value.isDefined) MSSearchSettings_minspectra = value.get }

  private var MSSearchSettings_scale: Int = 1000
  def setMSSearchSettings_scale(value: Option[Int]) { if (value.isDefined) MSSearchSettings_scale = value.get }

  private var MSSearchSettings_maxmods: Int = 64
  def setMSSearchSettings_maxmods(value: Option[Int]) { if (value.isDefined) MSSearchSettings_maxmods = value.get }

  private var MSSearchSettings_chargehandling: MSSearchSettings_chargehandling = new MSSearchSettings_chargehandling(new MSChargeHandle())
  def setMSSearchSettings_chargehandling(calcplusone: Option[String] = None, calccharge: Option[String] = None, mincharge: Option[Int] = None, maxcharge: Option[Int] = None, considermult: Option[Int] = None, plusone: Option[Double] = None, maxproductcharge: Option[Int] = None, negative: Option[Int] = None) {
    if (calcplusone.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_calcplusone = new MSChargeHandle_calcplusone(OmssaLists.calcPlusOne(calcplusone.get))
    if (calccharge.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_calccharge = new MSChargeHandle_calccharge(OmssaLists.calcPlusOne(calccharge.get))
    if (mincharge.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_mincharge = mincharge.get
    if (maxcharge.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_maxcharge = maxcharge.get
    if (considermult.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_considermult = considermult.get
    if (plusone.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_plusone = plusone.get
    if (maxproductcharge.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_maxproductcharge = maxproductcharge.get
    if (negative.isDefined) MSSearchSettings_chargehandling.MSChargeHandle.MSChargeHandle_negative = negative.get
  }

  private var MSSearchSettings_pseudocount: Int = 1
  def setMSSearchSettings_pseudocount(value: Option[Int]) { if (value.isDefined) MSSearchSettings_pseudocount = value.get }

  private var MSSearchSettings_searchb1: Int = 0
  def setMSSearchSettings_searchb1(value: Option[Boolean]) { if (value.isDefined) MSSearchSettings_searchb1 = if (value.get) 1 else 0 }

  private var MSSearchSettings_searchctermproduct: Int = 1
  def setMSSearchSettings_searchctermproduct(value: Option[Boolean]) { if (value.isDefined) MSSearchSettings_searchctermproduct = if (value.get) 1 else 0 }

  private var MSSearchSettings_maxproductions: Int = 100
  def setMSSearchSettings_maxproductions(value: Option[Int]) { if (value.isDefined) MSSearchSettings_maxproductions = value.get }

  private var MSSearchSettings_minnoenzyme: Int = 4
  def setMSSearchSettings_minnoenzyme(value: Option[Int]) { if (value.isDefined) MSSearchSettings_minnoenzyme = value.get }

  private var MSSearchSettings_maxnoenzyme: Int = 40
  def setMSSearchSettings_maxnoenzyme(value: Option[Int]) { if (value.isDefined) MSSearchSettings_maxnoenzyme = value.get }

  private var MSSearchSettings_exactmass: Double = 1446.94
  def setMSSearchSettings_exactmass(value: Option[Double]) { if (value.isDefined) MSSearchSettings_exactmass = value.get }

  private var MSSearchSettings_settingid: Int = 0
  def setMSSearchSettings_settingid(value: Option[Int]) { if (value.isDefined) MSSearchSettings_settingid = value.get }

  private var MSSearchSettings_iterativesettings: MSSearchSettings_iterativesettings = new MSSearchSettings_iterativesettings(new MSIterativeSettings())
  def setMSSearchSettings_iterativesettings(researchthresh: Option[Double] = None, subsetthresh: Option[Double] = None, replacethresh: Option[Double] = None) {
    if (researchthresh.isDefined) MSSearchSettings_iterativesettings.MSIterativeSettings.MSIterativeSettings_researchthresh = researchthresh.get
    if (subsetthresh.isDefined) MSSearchSettings_iterativesettings.MSIterativeSettings.MSIterativeSettings_subsetthresh = subsetthresh.get
    if (replacethresh.isDefined) MSSearchSettings_iterativesettings.MSIterativeSettings.MSIterativeSettings_replacethresh = replacethresh.get
  }

  private var MSSearchSettings_precursorcull: Int = 0
  def setMSSearchSettings_precursorcull(value: Option[Boolean]) { if (value.isDefined) MSSearchSettings_precursorcull = if (value.get) 1 else 0 }

  private var MSSearchSettings_infiles: MSSearchSettings_infiles = null
  def setMSSearchSettings_infiles(value: String) { MSSearchSettings_infiles = new MSSearchSettings_infiles(new MSInFile(value.replaceAll("\\\\", "/"), new MSInFile_infiletype(if (value.endsWith(".dta")) 0 else if (value.endsWith(".pkl")) 4 else 7))) }

  private var MSSearchSettings_outfiles: MSSearchSettings_outfiles = null
  def setMSSearchSettings_outfiles(value: String) { MSSearchSettings_outfiles = new MSSearchSettings_outfiles(new MSOutFile(MSOutFile_outfile = value.replaceAll("\\\\", "/"), MSOutFile_outfiletype = new MSOutFile_outfiletype(6), MSOutFile_includerequest = new MSOutFile_includerequest(true))) }

  private var MSSearchSettings_nocorrelationscore: Int = 1
  def setMSSearchSettings_nocorrelationscore(value: Option[Boolean]) { if (value.isDefined) MSSearchSettings_nocorrelationscore = if (value.get) 1 else 0 }

  private var MSSearchSettings_probfollowingion: Double = 0.5
  def setMSSearchSettings_probfollowingion(value: Option[Double]) { if (value.isDefined) MSSearchSettings_probfollowingion = value.get }

  private var MSSearchSettings_nmethionine: MSSearchSettings_nmethionine = new MSSearchSettings_nmethionine(true)
  def setMSSearchSettings_nmethionine(value: Option[Boolean]) { if (value.isDefined) MSSearchSettings_nmethionine = new MSSearchSettings_nmethionine(value.get) }

  private var MSSearchSettings_automassadjust: Int = 1
  def setMSSearchSettings_automassadjust(value: Option[Int]) { if (value.isDefined) MSSearchSettings_automassadjust = value.get }

  private var MSSearchSettings_numisotopes: Int = 0
  def setMSSearchSettings_numisotopes(value: Option[Int]) { if (value.isDefined) MSSearchSettings_numisotopes = value.get }

  private var MSSearchSettings_pepppm: MSSearchSettings_pepppm = new MSSearchSettings_pepppm(false)
  def setMSSearchSettings_pepppm(value: Option[String]) { if (value.isDefined) MSSearchSettings_pepppm = new MSSearchSettings_pepppm(value.get.toLowerCase().equals("ppm")) }

  private var MSSearchSettings_msmsppm: MSSearchSettings_msmsppm = new MSSearchSettings_msmsppm(false)
  def setMSSearchSettings_msmsppm(value: Option[String]) { if (value.isDefined) MSSearchSettings_msmsppm = new MSSearchSettings_msmsppm(value.get.toLowerCase().equals("ppm")) }

  private var MSSearchSettings_reportedhitcount: Int = 0
  def setMSSearchSettings_reportedhitcount(value: Option[Int]) { if (value.isDefined) MSSearchSettings_reportedhitcount = value.get }

}
