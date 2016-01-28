package fr.profi.jdbc

import java.util.Date

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Timestamp

import scala.collection.mutable.ArrayBuffer

import org.joda.time.DateTime
import org.joda.time.Duration

import fr.profi.util.primitives._

/**
 * Wraps a ResultSet in a row context. The ResultSetRow gives access
 * to the current row with no possibility to change row. The data of
 * the row can be accessed though the next<Type> methods which return
 * the optional value of the next column.
 */
class ResultSetRow(val rs: ResultSet) extends IValueContainer {
  
  def getBoolean(colLabel: String): Boolean = _getValue(colLabel,rs.getBoolean)
  def getInt(colLabel: String): Int = _getValue(colLabel,rs.getInt)
  def getLong(colLabel: String): Long = _getValue(colLabel,rs.getLong)
  def getFloat(colLabel: String): Float = _getValue(colLabel,rs.getFloat)
  def getDouble(colLabel: String): Double = _getValue(colLabel,rs.getDouble)
  def getBytes(colLabel: String): Array[Byte] = _getValue(colLabel,rs.getBytes )
  def getString(colLabel: String): String = _getValue(colLabel,rs.getString)
  def getTimestamp(colLabel: String): Timestamp = _getValue(colLabel,rs.getTimestamp)
  def getDate(colLabel: String): Date = _getValue(colLabel,rs.getTimestamp)
  def getAnyRef(colLabel: String): AnyRef = _getValue(colLabel,rs.getObject)
  def getAny(colLabel: String): Any = _getValue(colLabel,rs.getObject)
  def getAnyVal(colLabel: String): AnyVal = _getValue(colLabel,rs.getObject).asInstanceOf[AnyVal]
  
  def getBooleanOption(colLabel: String): Option[Boolean] = _getValueOption(colLabel,rs.getBoolean)
  def getIntOption(colLabel: String): Option[Int] = _getValueOption(colLabel,rs.getInt)
  def getLongOption(colLabel: String): Option[Long] = _getValueOption(colLabel,rs.getLong)
  def getFloatOption(colLabel: String): Option[Float] = _getValueOption(colLabel,rs.getFloat)
  def getDoubleOption(colLabel: String): Option[Double] = _getValueOption(colLabel,rs.getDouble)
  def getBytesOption(colLabel: String): Option[Array[Byte]] = _getValueOption(colLabel,rs.getBytes )
  def getStringOption(colLabel: String): Option[String] = _getValueOption(colLabel,rs.getString)
  def getTimestampOption(colLabel: String): Option[Timestamp] = _getValueOption(colLabel,rs.getTimestamp)
  def getDateOption(colLabel: String): Option[Date] = _getValueOption(colLabel,rs.getTimestamp)
  def getAnyRefOption(colLabel: String): Option[AnyRef] = _getValueOption(colLabel,rs.getObject)
  def getAnyOption(colLabel: String): Option[Any] = _getValueOption(colLabel,rs.getObject)
  def getAnyValOption(colLabel: String): Option[AnyVal] = _getValueOption(colLabel,rs.getObject).map( _.asInstanceOf[AnyVal] )
  
  def getBooleanOrElse( colLabel: String, value: => Boolean ): Boolean = _getValueOrElse(colLabel,rs.getBoolean,value)
  def getIntOrElse( colLabel: String, value: => Int ): Int = _getValueOrElse(colLabel,rs.getInt,value)
  def getLongOrElse( colLabel: String, value: => Long ): Long = _getValueOrElse(colLabel,rs.getLong,value)
  def getFloatOrElse( colLabel: String, value: => Float ): Float = _getValueOrElse(colLabel,rs.getFloat,value)
  def getDoubleOrElse( colLabel: String, value: => Double ): Double = _getValueOrElse(colLabel,rs.getDouble,value)
  def getBytesOrElse( colLabel: String, value: => Array[Byte] ): Array[Byte] = _getValueOrElse(colLabel,rs.getBytes,value)
  def getStringOrElse( colLabel: String, value: => String ): String = _getValueOrElse(colLabel,rs.getString,value)
  def getTimestampOrElse( colLabel: String, value: => Timestamp ): Timestamp = _getValueOrElse(colLabel,rs.getTimestamp,value)
  def getDateOrElse( colLabel: String, value: => Date ): Date = _getValueOrElse(colLabel,rs.getTimestamp,value)
  def getAnyRefOrElse( colLabel: String, value: => AnyRef ): AnyRef = _getValueOrElse(colLabel,rs.getObject,value)
  def getAnyOrElse( colLabel: String, value: => Any ): Any = _getValueOrElse(colLabel,rs.getObject,value)
  def getAnyValOrElse( colLabel: String, value: => AnyVal ): AnyVal = _getValueOrElse(colLabel,rs.getObject,value).asInstanceOf[AnyVal]
  
  /** Maintain the current column position. */
  private var _position = 0
  
  def nextBoolean: Boolean = _nextValue(rs.getBoolean)
  def nextInt: Int = _nextValue(rs.getInt)
  def nextLong: Long = _nextValue(rs.getLong)
  def nextFloat: Float = _nextValue(rs.getFloat)
  def nextDouble: Double = _nextValue(rs.getDouble)
  def nextBytes: Array[Byte] = _nextValue( rs.getBytes )
  def nextString: String = _nextValue(rs.getString)
  def nextTimestamp: Timestamp = _nextValue(rs.getTimestamp)
  def nextDate: Date = _nextValue(rs.getTimestamp)
  def nextAnyRef: AnyRef = _nextValue(rs.getObject)
  def nextAny: Any = _nextValue(rs.getObject)
  def nextAnyVal: AnyVal = _nextValue(rs.getObject).asInstanceOf[AnyVal]

  def nextBooleanOption: Option[Boolean] = _nextValueOption(rs.getBoolean)
  def nextIntOption: Option[Int] = _nextValueOption(rs.getInt)
  def nextLongOption: Option[Long] = _nextValueOption(rs.getLong)
  def nextFloatOption: Option[Float] = _nextValueOption(rs.getFloat)
  def nextDoubleOption: Option[Double] = _nextValueOption(rs.getDouble)
  def nextBytesOption: Option[Array[Byte]] = _nextValueOption( rs.getBytes )
  def nextStringOption: Option[String] = _nextValueOption(rs.getString)
  def nextTimestampOption: Option[Timestamp] = _nextValueOption(rs.getTimestamp)
  def nextDateOption: Option[Date] = _nextValueOption(rs.getTimestamp)
  def nextAnyRefOption: Option[AnyRef] = _nextValueOption(rs.getObject)
  def nextAnyOption: Option[Any] = _nextValueOption(rs.getObject)
  def nextAnyValOption: Option[AnyVal] = _nextValueOption(rs.getObject).map( _.asInstanceOf[AnyVal] )
  
  def nextBooleanOrElse( value: => Boolean ): Boolean = _nextValueOrElse(rs.getBoolean,value)
  def nextIntOrElse( value: => Int ): Int = _nextValueOrElse(rs.getInt,value)
  def nextLongOrElse( value: => Long ): Long = _nextValueOrElse(rs.getLong,value)
  def nextFloatOrElse( value: => Float ): Float = _nextValueOrElse(rs.getFloat,value)
  def nextDoubleOrElse( value: => Double ): Double = _nextValueOrElse(rs.getDouble,value)
  def nextBytesOrElse( value: => Array[Byte] ): Array[Byte] = _nextValueOrElse(rs.getBytes,value)
  def nextStringOrElse( value: => String ): String = _nextValueOrElse(rs.getString,value)
  def nextTimestampOrElse( value: => Timestamp ): Timestamp = _nextValueOrElse(rs.getTimestamp,value)
  def nextDateOrElse( value: => Date ): Date = _nextValueOrElse(rs.getTimestamp,value)
  def nextAnyRefOrElse( value: => AnyRef ): AnyRef = _nextValueOrElse(rs.getObject,value)
  def nextAnyOrElse( value: => Any ): Any = _nextValueOrElse(rs.getObject,value)
  def nextAnyValOrElse( value: => AnyVal ): AnyVal = _nextValueOrElse(rs.getObject,value).asInstanceOf[AnyVal]
  
  def toAnyMap(): AnyMap = {
    
    // Build the peptide record
    val record = new AnyMap()
    for( colName <- columnNames ) {
      record(colName) = this.nextAnyRefOrElse(null)
    }
    
    record
  }
  
  def wasNull = rs.wasNull()

  lazy val columnNames: Array[String] = {
    
    val columnNames = Array.newBuilder[String]
    val metaData = rs.getMetaData
    
    for (index <- 0.until(metaData.getColumnCount)) {
      columnNames += metaData.getColumnName(index + 1).toLowerCase
    }
    
    columnNames.result()
  }
  
  private def _getValue[T](colLabel: String, f: (String) => T): T = f(colLabel)
  
  def _getValueOption[T](colLabel: String, f: (String) => T): Option[T] = {
    val value = f(colLabel)
    if (rs.wasNull) None
    else Option(value)
  }
  
  private def _getValueOrElse[T](colLabel: String, f: (String) => T, defaultValue: => T): T = {    
    val value = f(colLabel)
    if (rs.wasNull) defaultValue
    else value
  }
  
  private def _incrementPosition = {
    _position = _position + 1
  }
  
  private def _nextValue[T](f: (Int) => T): T = {
    _incrementPosition
    f(_position)
  }

  private def _nextValueOption[T](f: (Int) => T): Option[T] = {
    _incrementPosition
    
    val value = f(_position)
    
    if (rs.wasNull) None
    else Option(value)
  }
  
  private def _nextValueOrElse[T](f: (Int) => T, defaultValue: => T): T = {
    _incrementPosition
    
    val value = f(_position)
    
    if (rs.wasNull) defaultValue
    else value
  }
  
}

object ResultSetRow {

  def apply(rs: ResultSet): ResultSetRow = {
    new ResultSetRow(rs)
  }
}
