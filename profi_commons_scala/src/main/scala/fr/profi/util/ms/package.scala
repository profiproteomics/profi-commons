package fr.profi.util

import scala.math.abs

package object ms {
  
  // Source : http://pdg.lbl.gov/2012/reviews/rpp2012-rev-phys-constants.pdf
  private val PROTON_MASS = 1.007276466812

  def mozToMass( moz: Double, charge: Int ): Double = ( moz * abs(charge) ) - charge * PROTON_MASS
  def massToMoz( mass: Double, charge: Int ): Double = (mass + charge * PROTON_MASS) / abs(charge)
  
  object MassTolUnit extends Enumeration {
    val Da = Value("Da")
    val mmu = Value("mmu")
    val PPM = Value("PPM")
  }
  
  def calcMozTolInDalton( moz: Double, mozTol: Double, massTolUnit: MassTolUnit.Value ): Double = {    
    massTolUnit match {
      case MassTolUnit.Da => mozTol
      case MassTolUnit.mmu => mozTol / 1000
      case MassTolUnit.PPM => mozTol * moz / 1000000
    }
  }
  
  def calcMozTolInDalton( moz: Double, mozTol: Double, tolUnitAsStr: String ): Double = {
    
    import scala.util.matching.Regex
    
    val DaType = """(?i)Da""".r
    val mmuType = """(?i)mmu""".r
    val PPMType = """(?i)PPM""".r
    
    tolUnitAsStr match {
      case DaType() => calcMozTolInDalton( moz, mozTol, MassTolUnit.Da )
      case mmuType() => calcMozTolInDalton( moz, mozTol, MassTolUnit.mmu )
      case PPMType() =>calcMozTolInDalton( moz, mozTol, MassTolUnit.PPM )
      case _ => throw new IllegalArgumentException("unknown tolerance unit: '" + tolUnitAsStr + "'")
    }

  }
  
}