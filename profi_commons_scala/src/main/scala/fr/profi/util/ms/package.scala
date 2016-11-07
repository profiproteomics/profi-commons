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
    val PPM = Value("ppm")
    
    implicit def string2unit(tolUnitAsStr: String): MassTolUnit.Value = {
       tolUnitAsStr match {
        case MassTolUnitRegex.DaUnit() => MassTolUnit.Da
        case MassTolUnitRegex.mmuUnit() => MassTolUnit.mmu 
        case MassTolUnitRegex.ppmUnit() => MassTolUnit.PPM
        case _ => throw new IllegalArgumentException(s"Unknown tolerance unit: '$tolUnitAsStr'")
      }
    }
  }
  
  private object MassTolUnitRegex {
    val DaUnit = """(?i)Da""".r
    val mmuUnit = """(?i)mmu""".r
    val ppmUnit = """(?i)ppm""".r
  }
  
  def calcMozTolInDalton( moz: Double, mozTol: Double, massTolUnit: MassTolUnit.Value ): Double = {    
    massTolUnit match {
      case MassTolUnit.Da => mozTol
      case MassTolUnit.mmu => mozTol / 1000
      case MassTolUnit.PPM => mozTol * moz / 1000000
    }
  }
  
  def calcMozTolInDalton( moz: Double, mozTol: Double, tolUnitAsStr: String ): Double = {
    
    tolUnitAsStr match {
      case MassTolUnitRegex.DaUnit() => calcMozTolInDalton( moz, mozTol, MassTolUnit.Da )
      case MassTolUnitRegex.mmuUnit() => calcMozTolInDalton( moz, mozTol, MassTolUnit.mmu )
      case MassTolUnitRegex.ppmUnit() =>calcMozTolInDalton( moz, mozTol, MassTolUnit.PPM )
      case _ => throw new IllegalArgumentException("unknown tolerance unit: '" + tolUnitAsStr + "'")
    }

  }
  
}