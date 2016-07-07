package fr.profi.chemistry.algo

import scala.collection.mutable.HashMap
import scala.collection.mutable.ArrayBuffer

import fr.profi.chemistry.model._
import fr.profi.util.lang.EnhancedEnum

object MassPrecision extends EnhancedEnum {
  val AVERAGE, MONOISOTOPIC = Value
}

/**
 * @author David Bouyssie
 *
 */
class MassComputer(
  val aaTable: IMolecularTable[AminoAcidResidue],
  val massPrecision: MassPrecision.Value = MassPrecision.MONOISOTOPIC
) {
  
  import MassPrecision._

  def calcPeptideMass(aaSequence: String): Double = {
    this.calcPeptideMass(aaSequence.toCharArray())
  }
  
  def calcPeptideMass(aaSequence: Array[Char]): Double = {
    require( aaSequence.nonEmpty, "aaSequence is empty" )
    
    val symbols = aaSequence.map(_.toString)
    
    // Add the C-terminal OH and N-Term H
    var mass = massPrecision match {
      case AVERAGE => MolecularConstants.WATER_AVERAGE_MASS
      case MONOISOTOPIC => MolecularConstants.WATER_MONO_MASS
    }
    
    // Add masses of symbols
    for (symbol <- symbols) {
      val aaOpt = aaTable.getMolecurlarEntityOpt(symbol)
      require(aaOpt.isDefined, s"The mass of the symbol $symbol is unknown")
      
      massPrecision match {
        case AVERAGE => mass += aaOpt.get.averageMass
        case MONOISOTOPIC => mass += aaOpt.get.monoMass
      }
    }
    
    mass
  }
  
  

}