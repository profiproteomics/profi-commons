package fr.profi.util

import org.junit.Assert._
import org.junit.Test

import fr.profi.util.primitives._

import fr.profi.util.{ MathUtils => JavaMathUtils }

@Test
class PrimitivesTest {

  val flValue: Float = 1.2345f

  val dblValue: Double = 1.2345

  @Test
  def testInt() {
    assertEquals("Java Long to Scala Int", 273, toInt(java.lang.Long.valueOf(273L)))

    assertEquals("String to Scala Int", -528, toInt("-528"))
  }

  @Test
  def testFloat() {
    assertEquals("Primitive Scala Float to Scala Float", flValue, toFloat(flValue), JavaMathUtils.EPSILON_FLOAT)
    assertEquals("Primitive Scala Float to AnyVal to Scala Float", flValue, valToFloat(flValue), JavaMathUtils.EPSILON_FLOAT)

    assertEquals("Primitive Scala Double to Scala Float", flValue, toFloat(dblValue), JavaMathUtils.EPSILON_FLOAT)
    assertEquals("Primitive Scala Double to AnyVal to Scala Float", flValue, valToFloat(dblValue), JavaMathUtils.EPSILON_FLOAT)

    assertEquals("Java Float Wrapper to Scala Float", flValue, toFloat(java.lang.Float.valueOf(flValue)), JavaMathUtils.EPSILON_FLOAT)
    assertEquals("Java Float Wrapper to AnyVal to Scala Float", flValue, valToFloat(java.lang.Float.valueOf(flValue)), JavaMathUtils.EPSILON_FLOAT)

    assertEquals("Java Double Wrapper to Scala Float", flValue, toFloat(java.lang.Double.valueOf(dblValue)), JavaMathUtils.EPSILON_FLOAT)
    assertEquals("Java Double Wrapper to AnyVal to Scala Float", flValue, valToFloat(java.lang.Double.valueOf(dblValue)), JavaMathUtils.EPSILON_FLOAT)
  }

  @Test
  def testDouble() {
    assertEquals("Primitive Scala Float to Scala Double", dblValue, toDouble(flValue), JavaMathUtils.EPSILON_LOW_PRECISION)
    assertEquals("Primitive Scala Float to AnyVal to Scala Double", dblValue, valToDouble(flValue), JavaMathUtils.EPSILON_LOW_PRECISION)

    assertEquals("Primitive Scala Double to Scala Double", dblValue, toDouble(dblValue), JavaMathUtils.EPSILON_HIGH_PRECISION)
    assertEquals("Primitive Scala Double to AnyVal to Scala Double", dblValue, valToDouble(dblValue), JavaMathUtils.EPSILON_HIGH_PRECISION)

    assertEquals("Java Float Wrapper to Scala Double", dblValue, toDouble(java.lang.Float.valueOf(flValue)), JavaMathUtils.EPSILON_LOW_PRECISION)
    assertEquals("Java Float Wrapper to AnyVal to Scala Double", dblValue, valToDouble(java.lang.Float.valueOf(flValue)), JavaMathUtils.EPSILON_LOW_PRECISION)

    assertEquals("Java Double Wrapper to Scala Double", dblValue, toDouble(java.lang.Double.valueOf(dblValue)), JavaMathUtils.EPSILON_HIGH_PRECISION)
    assertEquals("Java Double Wrapper to AnyVal to Scala Double", dblValue, valToDouble(java.lang.Double.valueOf(dblValue)), JavaMathUtils.EPSILON_HIGH_PRECISION)
  }

  def valToFloat(value: AnyVal): Float = {
    toFloat(value)
  }

  def valToDouble(value: AnyVal): Double = {
    toDouble(value)
  }
  
  @Test
  def testParseString() {
    assertTrue( parseString("true").isInstanceOf[Boolean] )
    assertTrue( parseString("0").isInstanceOf[Int] )
    assertTrue( parseString("11000000000000000").isInstanceOf[Long] )
    assertTrue( parseString("0.1").isInstanceOf[Float] )
    assertTrue( parseString("11000000000000000.0000000000000000001").isInstanceOf[Float] )
    assertTrue( parseString("Hello Proline !").isInstanceOf[String] )
  }

}
