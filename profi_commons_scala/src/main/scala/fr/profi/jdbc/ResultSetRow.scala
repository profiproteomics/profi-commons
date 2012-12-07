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

  def nextBoolean: Option[Boolean] = _nextValueOption(rs.getBoolean)
  def nextInt: Option[Int] = _nextValueOption(rs.getInt)
  def nextLong: Option[Long] = _nextValueOption(rs.getLong)
  def nextFloat: Option[Float] = _nextValueOption(rs.getFloat)
  def nextDouble: Option[Double] = _nextValueOption(rs.getDouble)
  def nextString: Option[String] = _nextValueOption(rs.getString)
  def nextTimestamp: Option[Timestamp] = _nextValueOption(rs.getTimestamp)
  def nextDate: Option[Date] = _nextValueOption(rs.getTimestamp)
  def nextObject: Option[AnyRef] = _nextValueOption(rs.getObject)
  
  def nextBooleanOrElse( value: Boolean ): Boolean = _nextValueOrElse(rs.getBoolean,value)
  def nextIntOrElse( value: Int ): Int = _nextValueOrElse(rs.getInt,value)
  def nextLongOrElse( value: Long ): Long = _nextValueOrElse(rs.getLong,value)
  def nextFloatOrElse( value: Float ): Float = _nextValueOrElse(rs.getFloat,value)
  def nextDoubleOrElse( value: Double ): Double = _nextValueOrElse(rs.getDouble,value)
  def nextStringOrElse( value: String ): String = _nextValueOrElse(rs.getString,value)
  def nextTimestampOrElse( value: Timestamp ): Timestamp = _nextValueOrElse(rs.getTimestamp,value)
  def nextDateOrElse( value: Date ): Date = _nextValueOrElse(rs.getTimestamp,value)
  def nextObjectOrElse( value: AnyRef ): AnyRef = _nextValueOrElse(rs.getObject,value)

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
/**
 * Defines a number of implicit conversion methods for the supported ColumnTypes. A call
 * to one of these methods will return the next value of the right type. The methods make
 * it easy to step through a row in order to build an object from it as shown in the example
 * below.
 *
 * Handles all types supported by jdbc as well as Option variants of those.
 *
 *     import fr.profi.jdbc.ResultSetRowImplicits._
 *
 *     case class Person( id: Long, name: String, birthdate: DateTime )
 *
 *     InTransaction { tx =>
 *         tx.select( "select id, name, birthdate from people" ) { r =>
 *             Person( r, r, r )
 *         }
 *     }
 */
object ResultSetRowImplicits {
  implicit def row2Boolean(row: ResultSetRow) = BooleanColumnType(row).nextValue
  implicit def row2Int(row: ResultSetRow): Int = IntColumnType(row).nextValue
  implicit def row2Long(row: ResultSetRow): Long = LongColumnType(row).nextValue
  implicit def row2Float(row: ResultSetRow) = FloatColumnType(row).nextValue
  implicit def row2Double(row: ResultSetRow) = DoubleColumnType(row).nextValue
  implicit def row2String(row: ResultSetRow) = StringColumnType(row).nextValue
  implicit def row2Date(row: ResultSetRow) = DateColumnType(row).nextValue
  implicit def row2Timestamp(row: ResultSetRow) = TimestampColumnType(row).nextValue
  implicit def row2DateTime(row: ResultSetRow) = DateTimeColumnType(row).nextValue
  implicit def row2Duration(row: ResultSetRow) = DurationColumnType(row).nextValue

  implicit def row2BooleanOption(row: ResultSetRow) = BooleanColumnType(row).nextValueOption
  implicit def row2IntOption(row: ResultSetRow) = IntColumnType(row).nextValueOption
  implicit def row2LongOption(row: ResultSetRow) = LongColumnType(row).nextValueOption
  implicit def row2FloatOption(row: ResultSetRow) = FloatColumnType(row).nextValueOption
  implicit def row2DoubleOption(row: ResultSetRow) = DoubleColumnType(row).nextValueOption
  implicit def row2StringOption(row: ResultSetRow) = StringColumnType(row).nextValueOption
  implicit def row2DateOption(row: ResultSetRow) = DateColumnType(row).nextValueOption
  implicit def row2TimestampOption(row: ResultSetRow) = TimestampColumnType(row).nextValueOption
  implicit def row2DateTimeOption(row: ResultSetRow) = DateTimeColumnType(row).nextValueOption
  implicit def row2DurationOption(row: ResultSetRow) = DurationColumnType(row).nextValueOption
}