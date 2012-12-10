package fr.profi.jdbc

import java.util.Date

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import java.sql.Timestamp

import scala.collection.mutable.ArrayBuffer

import org.joda.time.DateTime
import org.joda.time.Duration

/**
 * Wraps a ResultSet in a row context. The ResultSetRow gives access
 * to the current row with no possibility to change row. The data of
 * the row can be accessed though the next<Type> methods which return
 * the optional value of the next column.
 */
class ResultSetRow(val rs: ResultSet) {
  
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
  def nextObject: AnyRef = _nextValue(rs.getObject)

  def nextBooleanOption: Option[Boolean] = _nextValueOption(rs.getBoolean)
  def nextIntOption: Option[Int] = _nextValueOption(rs.getInt)
  def nextLongOption: Option[Long] = _nextValueOption(rs.getLong)
  def nextFloatOption: Option[Float] = _nextValueOption(rs.getFloat)
  def nextDoubleOption: Option[Double] = _nextValueOption(rs.getDouble)
  def nextBytesOption: Option[Array[Byte]] = _nextValueOption( rs.getBytes )
  def nextStringOption: Option[String] = _nextValueOption(rs.getString)
  def nextTimestampOption: Option[Timestamp] = _nextValueOption(rs.getTimestamp)
  def nextDateOption: Option[Date] = _nextValueOption(rs.getTimestamp)
  def nextObjectOption: Option[AnyRef] = _nextValueOption(rs.getObject)
  
  def nextBooleanOrElse( value: Boolean ): Boolean = _nextValueOrElse(rs.getBoolean,value)
  def nextIntOrElse( value: Int ): Int = _nextValueOrElse(rs.getInt,value)
  def nextLongOrElse( value: Long ): Long = _nextValueOrElse(rs.getLong,value)
  def nextFloatOrElse( value: Float ): Float = _nextValueOrElse(rs.getFloat,value)
  def nextDoubleOrElse( value: Double ): Double = _nextValueOrElse(rs.getDouble,value)
  def nextBytesOrElse( value: Array[Byte] ): Array[Byte] = _nextValueOrElse(rs.getBytes,value)
  def nextStringOrElse( value: String ): String = _nextValueOrElse(rs.getString,value)
  def nextTimestampOrElse( value: Timestamp ): Timestamp = _nextValueOrElse(rs.getTimestamp,value)
  def nextDateOrElse( value: Date ): Date = _nextValueOrElse(rs.getTimestamp,value)
  def nextObjectOrElse( value: AnyRef ): AnyRef = _nextValueOrElse(rs.getObject,value)
  
  def wasNull = rs.wasNull

  lazy val columnNames: Array[String] = {
    
    val columnNames = Array.newBuilder[String]
    val metaData = rs.getMetaData
    
    for (index <- 0.until(metaData.getColumnCount)) {
      columnNames += metaData.getColumnName(index + 1).toLowerCase
    }
    
    columnNames.result()
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
    else Some(value)
  }
  
  private def _nextValueOrElse[T](f: (Int) => T, defaultValue: T): T = {
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
