package fr.profi.chemistry.model

import scala.beans.BeanProperty
import fr.profi.util.misc.InMemoryIdGen

object Enzyme extends InMemoryIdGen

case class Enzyme(
    
  // Required fields
  var id: Long,
  val name: String,
  val enzymeCleavages: Array[EnzymeCleavage] = Array(),
  val cleavageRegexp: Option[String] = None,
  val isIndependant: Boolean = false,
  val isSemiSpecific: Boolean = false,
  val properties: Option[EnzymeProperties] = None
  
) {
  
  def this( name: String ) = {
    this( Enzyme.generateNewId, name)
  }

}

case class EnzymeProperties(
  @BeanProperty var ctermGain: Option[String] = None,
  @BeanProperty var ntermGain: Option[String] = None,
  @BeanProperty var minDistance: Option[Int] = None,
  @BeanProperty var maxMissedCleavages: Option[Int] = None
)

object EnzymeCleavage extends InMemoryIdGen

case class EnzymeCleavage(
    
  // Required fields
  var id: Long,
  val site: String,
  val residues: String,
  val restrictiveResidues: Option[String] = None

) {
  override def toString: String = {
    if(restrictiveResidues.isDefined && !restrictiveResidues.get.isEmpty())
      site + ":" + residues + "/" + restrictiveResidues.get
    else
      site + ":" + residues
  }
}