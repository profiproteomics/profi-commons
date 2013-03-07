package fr.proline.util.math

object MathUtils {

  /**
   * Tries to cast any value to Scala Float primitive.
   */
  def toFloat(value: Any): Float = {

    if (value.isInstanceOf[Float]) {
      value.asInstanceOf[Float]
    } else {
      value.asInstanceOf[Number].floatValue
    }

  }

  /**
   * Tries to cast any value to Scala Double primitive.
   */
  def toDouble(value: Any): Double = {

    if (value.isInstanceOf[Double]) {
      value.asInstanceOf[Double]
    } else {
      value.asInstanceOf[Number].doubleValue
    }

  }

}
