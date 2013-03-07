package fr.proline.util.math

object MathUtils {

  /**
   * Tries to cast any value to Scala Float primitive.
   */
  def toFloat(value: Any): Float = {

    if (value.isInstanceOf[Float]) {
      value.asInstanceOf[Float]
    } else if (value.isInstanceOf[Number]) {
      value.asInstanceOf[Number].floatValue
    } else {
      throw new IllegalArgumentException("Value is not a Number")
    }

  }

  /**
   * Tries to cast any value to Scala Double primitive.
   */
  def toDouble(value: Any): Double = {

    if (value.isInstanceOf[Double]) {
      value.asInstanceOf[Double]
    } else if (value.isInstanceOf[Number]) {
      value.asInstanceOf[Number].doubleValue
    } else {
      throw new IllegalArgumentException("Value is not a Number")
    }

  }

}
