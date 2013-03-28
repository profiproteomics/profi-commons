package fr.proline.util

package object primitives {

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
    
    value match {
      case d: Double => d
      case f: Float => f.toDouble
      case i: Int => i.toDouble
      case l: Long => l.toDouble
      case _ => value.asInstanceOf[Number].doubleValue
    }

  }

  /**
   * Tries to cast any value to Scala Int primitive (check if a long value can be casted into integer range).
   */
  def toInt(value: Any): Int = {

    if (value.isInstanceOf[Int]) {
      value.asInstanceOf[Int]
    } else {
      val longValue = value.asInstanceOf[Number].longValue

      if ((longValue < Integer.MIN_VALUE) || (longValue > Integer.MAX_VALUE)) {
        throw new IllegalArgumentException("Integer value out of range")
      }

      longValue.asInstanceOf[Int]
    }

  }

}
