package fr.profi.chemistry.algo

import fr.profi.chemistry.model._
import fr.profi.util.lang.EnhancedEnum

object MassPrecision extends EnhancedEnum {
  val AVERAGE, MONOISOTOPIC = Value
}

object MassUtil {

  def getProteinogenicMass(sequence : String) : Double = {
    val massComputer = new MassComputer(ProteinogenicAminoAcidTable)
    massComputer.computeMass(sequence)
  }
}
/**
 * @author David Bouyssie
 *
 */
class MassComputer(
  val aaTable: AminoAcidTableLike,
  val massPrecision: MassPrecision.Value = MassPrecision.MONOISOTOPIC
) {
  
  import MassPrecision._
  
  def computeMass(aaSequence: String): Double = {
    require( aaSequence.nonEmpty, "aaSequence is empty" )
    
    val aaCompo = new AminoAcidComposition(aaSequence, aaTable)
    
    massPrecision match {
      case AVERAGE => aaCompo.getAverageMass()
      case MONOISOTOPIC => aaCompo.getMonoMass()
    }
    
  }

  /*def computeMass(aaSequence: String): Double = {
    this.computeMass(aaSequence.toCharArray())
  }
  
  def computeMass(aaSequence: Array[Char]): Double = {
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
      require(aaOpt.isDefined, s"The symbol $symbol is unknown, can't retrieve a corresponding amino acid !")
      
      massPrecision match {
        case AVERAGE => mass += aaOpt.get.averageMass
        case MONOISOTOPIC => mass += aaOpt.get.monoMass
      }
    }
    
    mass
  }*/

}