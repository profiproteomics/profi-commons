package fr.profi.chemistry

import com.typesafe.scalalogging.StrictLogging
import org.junit._
import fr.profi.chemistry.model._

class MassComputerTest extends StrictLogging {
  
  @Test
  def computeMassesUsingHumanAminoAcidTable() = {
    
    val sequenceAndMassList = Array(
      ("STELLIR",830.48617),
      ("FTAVQALSVIESSK",1478.7980)
    )
    
    val massComputer = new fr.profi.chemistry.algo.MassComputer(HumanAminoAcidTable)

    for ((sequence,expectedMass) <- sequenceAndMassList) {
      logger.info("Peptide sequence = " + sequence)
  
      val mass = massComputer.calcPeptideMass(sequence)
      logger.info("Computed mass = " + mass)
      
      Assert.assertEquals(expectedMass, mass, 0.01)
    }

  }
  
  @Test
  def computeMassesUsingFullTable() = {
    
    val sequenceAndMassList = Array(
      ("STELLJR",830.48617),
      ("PUTZBIJOUX",1335.42)
    )
    
    val massComputer = new fr.profi.chemistry.algo.MassComputer(ProteinogenicAminoAcidTable)

    for ((sequence,expectedMass) <- sequenceAndMassList) {
      logger.info("Peptide sequence = " + sequence)
  
      val mass = massComputer.calcPeptideMass(sequence)
      logger.info("Computed mass = " + mass)
      
      Assert.assertEquals(expectedMass, mass, 0.01)
    }

  }

}