package fr.profi.util

import org.junit.Assert._
import org.junit.Test
import org.scalatestplus.junit.AssertionsForJUnit

import fr.profi.util.random._

@Test
class RandomTest extends AssertionsForJUnit {
  
  @Test
  def testRandomString() {
    assertEquals( "single char random string", "a", randomString("a",1,1) )
    assertEquals( "two chars random string", "aa", randomString("a",2,2) )
    
    intercept[IllegalArgumentException]( randomString(null,1,1) )
    intercept[IllegalArgumentException]( randomString("",1,1) )
  }
  
  @Test
  def testRandomInt() {
    val randInt: Int = randomInt(-10, 20)
    assert( randInt >= -10 )
    assert( randInt <= 20 )
  }
  
  @Test
  def testRandomFloat() {
    val randFloat: Float = randomFloat(-10, 20)
    assert( randFloat >= -10 )
    assert( randFloat <= 20 )
  }
  
  @Test
  def testRandomDouble() {
    val randDouble: Double = randomDouble(-10, 20)
    assert( randDouble >= -10 )
    assert( randDouble <= 20 )
  }

  @Test
  def testRandomGaussian() :Unit = {
    val randNumber: Double = randomGaussian(-10, 20,100)
    assert( randNumber >= -10 )
    assert( randNumber <= 20 )
  }
  
}