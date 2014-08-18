package fr.profi.util

import org.junit.Test
import org.scalatest.junit.JUnitSuite

import fr.profi.util.ms._

@Test
class MsUtilsTest extends JUnitSuite {
  
  @Test
  def testMozToMass() = {
    assert( mozToMass( 482.2, 1 ) === 481.192723533188 )
    assert( mozToMass( 482.2, 2 ) === 962.385447066376 )
    assert( mozToMass( 482.2, 3 ) === 1443.578170599564 )
  }
  

  @Test
  def testMassToMoz() = {
    assert( massToMoz( 481.192723533188, 1 ) === 482.2 )
    assert( massToMoz( 962.385447066376, 2 ) === 482.2 )
    assert( massToMoz( 1443.578170599564, 3 ) === 482.2 )
  }
  
  @Test
  def testCalcMozTolInDalton() = {
    assert( calcMozTolInDalton( 482.2, 0.1, "Da" ) === 0.1 )
    assert( calcMozTolInDalton( 482.2, 10, "mmu" ) === 0.01 )
    assert( calcMozTolInDalton( 482.2, 10, "ppm" ) === 0.004822 )
  }
  
}
