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
      case v: Number => checkFloat(v.doubleValue)
      case v: String => checkFloat(java.lang.Double.parseDouble(normalizeString(v)))
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
  
  private def checkFloat(doubleValue: Double): Float = {

    if ((doubleValue < java.lang.Float.MIN_VALUE) || (doubleValue > java.lang.Float.MAX_VALUE)) {
      throw new IllegalArgumentException("Float value out of range")
    }

    doubleValue.asInstanceOf[Float]
  }
  
  private def getTypeAsString(value: Any): String = {
    val valueType = try {
      value.asInstanceOf[AnyRef].getClass.toString
    } catch {
      case e: Throwable => "unknown"
    }
    
    valueType
  }
  
  // TODO: handle date type ??
  object DataType extends Enumeration {
    val NULL = Value("NULL")
    val INTEGER = Value("INTEGER")
    val DECIMAL = Value("DECIMAL")
    val BOOLEAN = Value("BOOLEAN")
    val STRING = Value("STRING")
  }
  
  def parseString( str: String ): Any = {
    
    import DataType._
    
    val dataType = inferDataType(str)
    
    dataType match  {
      case STRING => str
      case NULL => str
      case BOOLEAN => str.toBoolean
      case INTEGER => {
        try {
          toInt(str)
        } catch {
          case e: Exception => toLong(str)
        }
      }
      case DECIMAL => {
        try {
          toFloat(str)
        } catch {
          case e: Exception => toDouble(str)
        }
      }
      case _ => throw new Exception("invalid data type")
    }

  }
  
  // TODO: handle scientific notation (i.e. 1e6)
  def inferDataType( str: String ): DataType.Value = {
    if (str == null) return DataType.NULL
    
    val length = str.length
    if (length == 0) return DataType.NULL
    
    if( str == "true" || str == "false" ) return DataType.BOOLEAN
    
    var dataType = DataType.INTEGER
    var c: Char = '\0'
    var i = 0
    var hasDot = false
    
    if (str.charAt(0) == '-') {
      i = 1
    }
    
    while( i < length ) {
      
      c = str.charAt(i)
      
      if (c < '0' || c > '9') {
        // TODO: handle ',' character ???
        if( c == '.' ) {
          if( hasDot ) return DataType.STRING
          else {
            dataType = DataType.DECIMAL
          }
        } else {
          return DataType.STRING
        }
      }
      
      i += 1
    }
    
    dataType
  }

}
