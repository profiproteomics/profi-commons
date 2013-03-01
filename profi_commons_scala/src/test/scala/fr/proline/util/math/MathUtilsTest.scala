package fr.proline.util.math

import org.junit.Assert.assertEquals
import org.junit.Test

import fr.proline.util.{MathUtils => JavaMathUtils}

@Test
class MathUtilsTest {

  val flValue: Float = 1.2345f

  val dblValue: Double = 1.2345

  @Test
  def testFloat() {
    assertEquals("Primitive Scala Float to Scala Float", flValue, MathUtils.toFloat(flValue), JavaMathUtils.EPSILON_FLOAT)
    assertEquals("Primitive Scala Double to Scala Float", flValue, MathUtils.toFloat(dblValue), JavaMathUtils.EPSILON_FLOAT)
    assertEquals("Java Float Wrapper to Scala Float", flValue, MathUtils.toFloat(java.lang.Float.valueOf(flValue)), JavaMathUtils.EPSILON_FLOAT)
    assertEquals("Java Double Wrapper to Scala Float", flValue, MathUtils.toFloat(java.lang.Double.valueOf(dblValue)), JavaMathUtils.EPSILON_FLOAT)
  }

  @Test
  def testDouble() {
    assertEquals("Primitive Scala Float to Scala Double", dblValue, MathUtils.toDouble(flValue), JavaMathUtils.EPSILON_LOW_PRECISION)
    assertEquals("Primitive Scala Double to Scala Double", dblValue, MathUtils.toDouble(dblValue), JavaMathUtils.EPSILON_HIGH_PRECISION)
    assertEquals("Java Float Wrapper to Scala Double", dblValue, MathUtils.toDouble(java.lang.Float.valueOf(flValue)), JavaMathUtils.EPSILON_LOW_PRECISION)
    assertEquals("Java Double Wrapper to Scala Double", dblValue, MathUtils.toDouble(java.lang.Double.valueOf(dblValue)), JavaMathUtils.EPSILON_HIGH_PRECISION)
  }

}
