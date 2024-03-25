package fr.profi.ms

import com.typesafe.scalalogging.StrictLogging
import fr.profi.chemistry.model.{BiomoleculeAtomTable, HumanAminoAcidTable}
import fr.profi.ms.algo._
import org.junit.{FixMethodOrder, Test}
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class IsotopePatternTest extends StrictLogging {

  val aaComp = new fr.profi.chemistry.model.AminoAcidComposition(
    "QFHEANNMTDALAALSAAVAAQLPCR",
    HumanAminoAcidTable
  )
  val atomComp = aaComp.getAtomComposition(BiomoleculeAtomTable)

  val monoMass = aaComp.getMonoMass()
  val atomMonoMass = atomComp.getMonoMass()
  // FIXME: monoMass should equal atomMonoMass

  /*@Before
  @throws(classOf[Exception])
  def setUp() = {
    
  }*/

  @Test
  def a_patternComputer() = {

    val atomTable = BiomoleculeAtomTable

    // Compute the averagine atom composition
    //val averagineComputer = new fr.profi.chemistry.algo.AveragineComputer(aaTable, atomTable)
    //val averagine = averagineComputer.computeAveragine(2600.0, adjustAbundances = true)._1

    // Create a map defining the maximum number of atoms
    val maxAtomCountByAtom = Map(
      atomTable.getAtom("C") -> 1000,
      atomTable.getAtom("H") -> 2000,
      atomTable.getAtom("O") -> 1000,
      atomTable.getAtom("N") -> 1000,
      atomTable.getAtom("P") -> 10,
      atomTable.getAtom("S") -> 3,
      atomTable.getAtom("Se") -> 1
    )

    // Compute the isotopic variant combinations required for isotope distribution computation
    val combinations = IsotopeDistributionComputer.computeIsotopicVariantCombinations(maxAtomCountByAtom, 0.00001f)
    val computer = IsotopeDistributionComputer

    val t0 = System.currentTimeMillis()
    var t1 = System.currentTimeMillis()

    var i = 0
    while (i < 100) {

      // Compute the isotope distribution
      val theoDistrib = computer.computeIsotopeDistribution(atomComp, 1, combinations, 0.01f)
      val theoPattern = theoDistrib.theoIsotopePattern
      t1 = System.currentTimeMillis()

      if (i == 0) {
        theoPattern.mzAbundancePairs.foreach { p =>
          logger.info(p._1 + " , " + p._2)
        }
      }

      i += 1
    }

    logger.info("Isotope pattern computation took: " + (t1 - t0) / 1000f)
  }

  @Test
  def b_patternInterpolator() = {

    val interpolator = IsotopePatternInterpolator

    val t0 = System.currentTimeMillis()
    var t1 = System.currentTimeMillis()
    var i = 0
    while (i < 1000) {

      val theoPattern = interpolator.getTheoreticalPattern(atomMonoMass, 1)
      t1 = System.currentTimeMillis()
      if (i == 0) {
        theoPattern.mzAbundancePairs.foreach { p =>
          logger.info(p._1 + " , " + p._2)
        }
      }

      i += 1
    }

    logger.info("Isotope pattern interpolation took: " + (t1 - t0) / 1000f)
  }

  @Test
  def c_patternEstimator() = {

    val estimator = IsotopePatternEstimator

    val t0 = System.currentTimeMillis()
    var t1 = System.currentTimeMillis()
    var i = 0
    while (i < 1000) {

      val theoPattern = estimator.getTheoreticalPattern(atomMonoMass, 1)
      t1 = System.currentTimeMillis()
      if (i == 0) {
        theoPattern.mzAbundancePairs.foreach { p =>
          logger.info(p._1 + " , " + p._2)
        }
      }

      i += 1
    }

    logger.info("Isotope pattern estimation took: " + (t1 - t0) / 1000f)
  }

}