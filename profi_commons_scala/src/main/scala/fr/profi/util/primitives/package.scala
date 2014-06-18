package fr.profi.util

package object primitives {
  
  def isZeroOrNaN( value: Float ) = value.isNaN || value == 0f
  def isZeroOrNaN( value: Double ) = value.isNaN || value == 0d

  /**
   * Tries to cast any value to Scala Boolean primitive.
   */
  def toBoolean(value: Any): Boolean = {

    value match {
      case v: Boolean           => v
      case v: java.lang.Boolean => v.booleanValue
      case v: String            => java.lang.Boolean.parseBoolean(normalizeString(v))
      case _                    => throw new IllegalArgumentException("Type of value is "+getTypeAsString(value)+" not boolean")
    }

  }

  /**
   * Tries to cast any value to Scala Int primitive (check if a long value can be casted into integer range).
   */
  def toInt(value: Any): Int = {

    value match {
      case v: Int    => v
      case v: Number => checkInt(v.longValue)
      case v: String => checkInt(java.lang.Long.parseLong(normalizeString(v)))
      case _         => throw new IllegalArgumentException("Type of value is "+getTypeAsString(value)+" not integer")
    }

  }

  /**
   * Tries to cast any value to Scala Long primitive.
   */
  def toLong(value: Any): Long = {

    value match {
      case v: Long   => v
      case v: Number => v.longValue
      case v: String => java.lang.Long.parseLong(normalizeString(v))
      case _         => throw new IllegalArgumentException("Type of value is "+getTypeAsString(value)+" not long")
    }

  }

  /**
   * Tries to cast any value to Scala Float primitive.
   */
  def toFloat(value: Any): Float = {

    value match {
      case v: Float  => v
      case v: Number => v.floatValue
      case v: String => java.lang.Float.parseFloat(normalizeString(v))
      case _         => throw new IllegalArgumentException("Type of value is "+getTypeAsString(value)+" not float")
    }

  }

  /**
   * Tries to cast any value to Scala Double primitive.
   */
  def toDouble(value: Any): Double = {

    value match {
      case v: Double => v
      case v: Number => v.doubleValue
      case v: String => java.lang.Double.parseDouble(normalizeString(v))
      case _         => throw new IllegalArgumentException("Type of value is "+getTypeAsString(value)+" not double")
    }

  }

  private def normalizeString(rawStr: String): String = {

    if (StringUtils.isEmpty(rawStr)) {
      throw new IllegalArgumentException("Invalid raw String value")
    }

    rawStr.trim()
  }

  private def checkInt(longValue: Long): Int = {

    if ((longValue < Integer.MIN_VALUE) || (longValue > Integer.MAX_VALUE)) {
      throw new IllegalArgumentException("Integer value out of range")
    }

    longValue.asInstanceOf[Int]
  }
  
  private def getTypeAsString(value: Any): String = {
    val valueType = try {
      value.asInstanceOf[AnyRef].getClass.toString
    } catch {
      case e: Throwable => "unknown"
    }
    
    valueType
  }

}
