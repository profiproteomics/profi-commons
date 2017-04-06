package fr.profi.util

import java.sql.Timestamp
import java.util.Date
import java.util.regex.Pattern
import scala.collection.immutable.StringOps
import scala.collection.mutable.HashMap

package object primitives {
  
/*
 * Pattern for number detection
 * 
Pattern DOUBLE_PATTERN = Pattern.compile(
    "[\\x00-\\x20]*[+-]?(NaN|Infinity|((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)" +
    "([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|" +
    "(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))" +
    "[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*");
*/
  
  private val datePattern = Pattern.compile("\\d{4}-[01]\\d-[0-3]\\d")
  private val dateTimePattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}")
  
  private def newDateFormat() = new java.text.SimpleDateFormat("yyyy-MM-dd") // dateFormat.setLenient(false)
  private def newDateTimeFormat() = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
  private val localDateFormat = newDateFormat()
  private val localDateTimeFormat = newDateTimeFormat()
  
  def isZeroOrNaN( value: Float ) = value.isNaN || value == 0f
  def isZeroOrNaN( value: Double ) = value.isNaN || value == 0d

  def toIntOrZero(v: Any): Int = try { toInt(v) } catch { case e: Throwable => 0 }
  def toFloatOrMinusOne(v: Any): Float = try { toFloat(v) } catch { case e: Throwable => -1f }

  /**
   * Tries to cast any value to Scala Boolean primitive.
   */
  def toBoolean(value: Any): Boolean = {

    value match {
      case v: Boolean           => v
      case v: java.lang.Boolean => v.booleanValue
      // TODO: parse using StringOrBoolAsBool.asBoolean instead ???
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
  
  /**
   * Tries to cast any value to java.lang.String
   */
  def castToString(value: Any): String = {
    if (value == null) return value.asInstanceOf[String]

    value match {
      case v: String => v
      case _         => value.toString
    }

  }
  
  /**
   * Tries to cast any value to java.util.Date
   */
  def castToDate(value: Any): Date = {
    if (value == null) return value.asInstanceOf[Date]

    value match {
      case v: Date => v
      case v: String => localDateTimeFormat.parse(normalizeString(v))
      case _         => throw new IllegalArgumentException("Type of value is "+getTypeAsString(value)+" not Date")
    }

  }
  
  /**
   * Tries to cast any value to java.sql.Timestamp
   */
  def castToTimestamp(value: Any): Timestamp = {
    if (value == null) return value.asInstanceOf[Timestamp]

    value match {
      case v: Timestamp => v
      case v: Date => new java.sql.Timestamp(v.getTime)
      case v: String => new java.sql.Timestamp( localDateTimeFormat.parse(normalizeString(v)).getTime )
      case _         => throw new IllegalArgumentException("Type of value is "+getTypeAsString(value)+" not Date")
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
  

  trait IValueContainer extends Any {
    
    def getBoolean(key: String): Boolean
    def getInt(key: String): Int
    def getLong(key: String): Long
    def getFloat(key: String): Float
    def getDouble(key: String): Double
    def getBytes(key: String): Array[Byte]
    def getString(key: String): String
    def getTimestamp(key: String): Timestamp
    def getDate(key: String): Date
    def getAnyRef(key: String): AnyRef
    def getAny(key: String): Any
    def getAnyVal(key: String): AnyVal
    
    def getBooleanOption(key: String): Option[Boolean]
    def getIntOption(key: String): Option[Int]
    def getLongOption(key: String): Option[Long]
    def getFloatOption(key: String): Option[Float]
    def getDoubleOption(key: String): Option[Double]
    def getBytesOption(key: String): Option[Array[Byte]]
    def getStringOption(key: String): Option[String]
    def getTimestampOption(key: String): Option[Timestamp]
    def getDateOption(key: String): Option[Date]
    def getAnyRefOption(key: String): Option[AnyRef]
    def getAnyOption(key: String): Option[Any]
    def getAnyValOption(key: String): Option[AnyVal]
    
    def getBooleanOrElse( key: String, default: => Boolean ): Boolean
    def getIntOrElse( key: String, default: => Int ): Int
    def getLongOrElse( key: String, default: => Long ): Long
    def getFloatOrElse( key: String, default: => Float ): Float
    def getDoubleOrElse( key: String, default: => Double ): Double
    def getBytesOrElse( key: String, default: => Array[Byte] ): Array[Byte]
    def getStringOrElse( key: String, default: => String ): String
    def getTimestampOrElse( key: String, default: => Timestamp ): Timestamp
    def getDateOrElse( key: String, default: => Date ): Date
    def getAnyRefOrElse( key: String, default: => AnyRef ): AnyRef
    def getAnyOrElse( key: String, default: => Any ): Any
    def getAnyValOrElse( key: String, default: => AnyVal ): AnyVal 
  }
  
  // TODO: check the class http://grepcode.com/file_/repo1.maven.org/maven2/org.apache.wicket/wicket-util/6.13.0/org/apache/wicket/util/value/ValueMap.java/?v=source
  // There may be some interesting stuffs there
  trait AnyMapLike extends Any with IValueContainer {
    
    import fr.profi.util.sql.StringOrBoolAsBool.asBoolean
    
    def apply(key: String): Any
    def contains(key: String): Boolean    
    def get(key: String): Option[Any]
    
    def getIfNotNull(key: String): Option[Any] = {
      val valOpt = this.get(key)
      if( valOpt.isEmpty ) None
      else {
        val value = valOpt.get
        if( value == null ) None
        else Some(value)
      }
    }
    
    def isDefined(key: String) = this.contains(key) && this(key) != null
  
    def getBoolean(key: String): Boolean = asBoolean( this(key) )
    def getInt(key: String): Int = toInt( this(key) )
    def getLong(key: String): Long = toLong( this(key) )
    def getFloat(key: String): Float = toFloat( this(key) )
    def getDouble(key: String): Double = toDouble( this(key) )
    def getBytes(key: String): Array[Byte] = this(key).asInstanceOf[Array[Byte]]
    def getString(key: String): String = castToString( this(key) )
    def getTimestamp(key: String): Timestamp = castToTimestamp( this(key) )
    def getDate(key: String): Date = castToDate( this(key) )
    def getAnyRef(key: String): AnyRef = this(key).asInstanceOf[AnyRef]
    def getAny(key: String): Any = this(key)
    def getAnyVal(key: String): AnyVal = this(key).asInstanceOf[AnyVal]
    
    def getBooleanOption(key: String): Option[Boolean] = getIfNotNull(key).map( asBoolean(_) )
    def getIntOption(key: String): Option[Int] = getIfNotNull(key).map( toInt(_) )
    def getLongOption(key: String): Option[Long] = getIfNotNull(key).map( toLong(_) )
    def getFloatOption(key: String): Option[Float] = getIfNotNull(key).map( toFloat(_) )
    def getDoubleOption(key: String): Option[Double] = getIfNotNull(key).map( toDouble(_) )
    def getBytesOption(key: String): Option[Array[Byte]] = getIfNotNull(key).map( _.asInstanceOf[Array[Byte]] )
    def getStringOption(key: String): Option[String] = getIfNotNull(key).map( _.toString )
    def getTimestampOption(key: String): Option[Timestamp] = getIfNotNull(key).map( castToTimestamp(_) )
    def getDateOption(key: String): Option[Date] = getIfNotNull(key).map( castToDate(_) )
    def getAnyRefOption(key: String): Option[AnyRef] = getIfNotNull(key).map( _.asInstanceOf[AnyRef] )
    def getAnyOption(key: String): Option[Any] = getIfNotNull(key)
    def getAnyValOption(key: String): Option[AnyVal] = getIfNotNull(key).map( _.asInstanceOf[AnyVal] )
    
    def getBooleanOrElse( key: String, default: => Boolean ): Boolean = getBooleanOption(key).getOrElse(default)
    def getIntOrElse( key: String, default: => Int ): Int = getIntOption(key).getOrElse(default)
    def getLongOrElse( key: String, default: => Long ): Long = getLongOption(key).getOrElse(default)
    def getFloatOrElse( key: String, default: => Float ): Float = getFloatOption(key).getOrElse(default)
    def getDoubleOrElse( key: String, default: => Double ): Double = getDoubleOption(key).getOrElse(default)
    def getBytesOrElse( key: String, default: => Array[Byte] ): Array[Byte] = getBytesOption(key).getOrElse(default)
    def getStringOrElse( key: String, default: => String ): String = getStringOption(key).getOrElse(default)
    def getTimestampOrElse( key: String, default: => Timestamp ): Timestamp = getTimestampOption(key).getOrElse(default)
    def getDateOrElse( key: String, default: => Date ): Date = getDateOption(key).getOrElse(default)
    def getAnyRefOrElse( key: String, default: => AnyRef ): AnyRef = getAnyRefOption(key).getOrElse(default)
    def getAnyOrElse( key: String, default: => Any ): Any = getAnyOption(key).getOrElse(default)
    def getAnyValOrElse( key: String, default: => AnyVal ): AnyVal = getAnyValOption(key).getOrElse(default)
  }
  
  class AnyMap() extends HashMap[String,Any] with AnyMapLike
  class AnyMapWrapper( val wrappedMap: Map[String,Any] ) extends AnyVal with AnyMapLike {
    def apply(key: String): Any = wrappedMap(key)
    def contains(key: String): Boolean = wrappedMap.contains(key)
    def get(key: String): Option[Any] = wrappedMap.get(key)
  }

  trait StringMapLike extends Any with AnyMapLike {
    
    override def apply(key: String): String
    override def get(key: String): Option[String]
  
    override def getBytes(key: String): Array[Byte] = this(key).getBytes // may require some post-processing (Base64 decoding)
    override def getString(key: String): String = this(key)
    override def getAnyRef(key: String): AnyRef = this(key)
    
    override def getBytesOption(key: String): Option[Array[Byte]] = this.get(key).map( _.getBytes )
    override def getStringOption(key: String): Option[String] = this.get(key)
    override def getAnyRefOption(key: String): Option[AnyRef] = this.get(key)
    override def getAnyValOption(key: String): Option[AnyVal] = this.get(key).map( s => new StringOps(s) )
  }
  
  class StringMap() extends HashMap[String,String] with StringMapLike
  class StringMapWrapper( val wrappedMap: Map[String,String] ) extends AnyVal with StringMapLike {
    def apply(key: String): String = wrappedMap(key)
    def contains(key: String): Boolean = wrappedMap.contains(key)
    def get(key: String): Option[String] = wrappedMap.get(key)
  }
  
  // TODO: handle date type ??
  object DataType extends Enumeration {
    val NULL = Value("NULL")
    val INTEGER = Value("INTEGER")
    val DECIMAL = Value("DECIMAL")
    val BOOLEAN = Value("BOOLEAN")
    val STRING = Value("STRING")
    val DATE = Value("DATE")
    val DATETIME = Value("DATETIME")
  }
  
  def parseString( str: String ): Any = {
    
    import DataType._
    
    val dataType = inferDataType( str )
    
    dataType match  {
      case STRING => {
        try {
          if( dateTimePattern.matcher(str).matches() ) {
            return localDateTimeFormat.parse(str)
          } else if( datePattern.matcher(str).matches() ) {
            return localDateFormat.parse(str)
          } else {
            return str
          }
        } catch {
          case e: Exception => return str
        }
      }
      case NULL => return str
      case BOOLEAN => return str.toBoolean
      case INTEGER => {
        try {
          return toInt(str)
        } catch {
          case e: Exception => return toLong(str)
        }
      }
      case DECIMAL => {
        
        val zeroStrippedStr = str.replaceFirst("\\.0*$|(\\.\\d*?)0+$", "$1")
        val numberOfSigD = zeroStrippedStr.replaceFirst("\\.", "").length
        
        if( numberOfSigD > 7 ) return str.toDouble
        else return str.toFloat        
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
    var c: Char = 0
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
  
  def isValidDate( text: String ): Boolean = {
    
    if ( text == null || datePattern.matcher(text).matches() == false )
      return false
    
    val df = newDateFormat()
    df.setLenient(false)
    
    try {
      df.parse(text)
      return true
    } catch {
      case e: Exception => return false
    }
    
  }
  
  def isValidDateTime( text: String ): Boolean = {
    
    if ( text == null || dateTimePattern.matcher(text).matches() == false )
      return false
    
    val df = newDateTimeFormat()
    df.setLenient(false)
    
    try {
      df.parse(text)
      return true
    } catch {
      case e: Exception => return false
    }
    
  }

}