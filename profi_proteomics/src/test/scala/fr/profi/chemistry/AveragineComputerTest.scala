package fr.profi.chemistry

import com.typesafe.scalalogging.StrictLogging
import org.junit._
import fr.profi.ms.algo.IsotopePatternInterpolator
import fr.profi.chemistry.model.HumanAminoAcidTable
import fr.profi.chemistry.model.BiomoleculeAtomTable

class AveragineComputerTest extends StrictLogging {

  @Before
  @throws(classOf[Exception])
  def setUp() = {

  }

  /*
  @Test
  def computeAveragineCBy() = {
    val formula = AveragineComputer.computeAveragine(2600.0)
    logger.info("C = " + formula(Elements.C))
    logger.info("H = " + formula(Elements.H))
    logger.info("N = " + formula(Elements.N))
    logger.info("O = " + formula(Elements.O))
    logger.info("S = " + formula(Elements.S))

    val m = formula(Elements.C) * Elements.C.massMonoistopic + formula(Elements.H) * Elements.H.massMonoistopic + formula(Elements.N) * Elements.N.massMonoistopic + formula(Elements.O) * Elements.O.massMonoistopic + formula(Elements.S) * Elements.S.massMonoistopic
    logger.info("mass = " + m)
    Assert.assertEquals(2600.0, m, 1.0)
  }*/

  @Test
  def computeChemistryAveragine() = {
    val computer = new fr.profi.chemistry.algo.AveragineComputer(HumanAminoAcidTable, BiomoleculeAtomTable)
    val composition = computer.computeAveragine(2600.0, adjustAbundances = false)._1
    logger.info("formula = " + composition.toFormula)

    val m = composition.getMonoMass
    logger.info("mass = " + m)
    Assert.assertEquals(2600.0, m, 1.0)

  }

}