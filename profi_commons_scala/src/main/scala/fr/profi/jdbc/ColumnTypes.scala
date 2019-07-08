package fr.profi.jdbc

import java.util.Date
import java.sql.Timestamp

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormatter


/**
 * Extract a value from a certain column type. Used internally
 * to implement support for custom data types like DateTime and
 * more.
 */
trait IColumnType[T] {
  
  /**
   * @throws RunTimeException if the value was null
   */
  @throws( classOf[Exception] )
  def nextValue: T = nextValueOption.getOrElse( throw new Exception("unexpected null value") )
  
  /**
   * To be implemented. Should return Some if the value
   * of the current column is not null, if it is return
   * None.
   *
   * @throws Exception if the column could not be intepreted as
   *         the implementing type.
   */
  def nextValueOption: Option[T]
}

trait ColumnTypeFactory[T] {
  def apply(row: ResultSetRow): IColumnType[T]
}

//
// String
//
class StringColumnType(row: ResultSetRow) extends IColumnType[String] {
  override def nextValueOption: Option[String] = row.nextStringOption
}
object StringColumnType extends ColumnTypeFactory[String] {
  def apply(row: ResultSetRow) = new StringColumnType(row)
}

//
// Boolean
// 
class BooleanColumnType(row: ResultSetRow) extends IColumnType[Boolean] {
  override def nextValueOption: Option[Boolean] = row.nextBooleanOption
}
object BooleanColumnType extends ColumnTypeFactory[Boolean] {
  def apply(row: ResultSetRow) = new BooleanColumnType(row)
}

//
// Long
//
class LongColumnType(row: ResultSetRow) extends IColumnType[Long] {
  override def nextValueOption: Option[Long] = row.nextLongOption
}
object LongColumnType extends ColumnTypeFactory[Long] {
  def apply(row: ResultSetRow) = new LongColumnType(row)
}

//
// Int
//
class IntColumnType(row: ResultSetRow) extends IColumnType[Int] {
  override def nextValueOption: Option[Int] = row.nextIntOption
}
object IntColumnType extends ColumnTypeFactory[Int] {
  def apply(row: ResultSetRow) = new IntColumnType(row)
}

//
// Float
//
class FloatColumnType(row: ResultSetRow) extends IColumnType[Float] {
  override def nextValueOption: Option[Float] = row.nextFloatOption
}
object FloatColumnType extends ColumnTypeFactory[Float] {
  def apply(row: ResultSetRow) = new FloatColumnType(row)
}

//
// Double
//
class DoubleColumnType(row: ResultSetRow) extends IColumnType[Double] {
  override def nextValueOption: Option[Double] = row.nextDoubleOption
}
object DoubleColumnType extends ColumnTypeFactory[Double] {
  def apply(row: ResultSetRow) = new DoubleColumnType(row)
}

//
// Bytes
//
class BytesColumnType( row: ResultSetRow ) extends IColumnType[Array[Byte]]{
  override def nextValueOption: Option[Array[Byte]] = row.nextBytesOption
}
object BytesColumnType extends ColumnTypeFactory[Array[Byte]] {
    def apply( row: ResultSetRow ) = new BytesColumnType( row )
}

//
// Date
//
class DateColumnType(row: ResultSetRow) extends IColumnType[Date] {
  override def nextValueOption: Option[Date] = row.nextDateOption
}
object DateColumnType extends ColumnTypeFactory[Date] {
  def apply(row: ResultSetRow) = new DateColumnType(row)
}

//
// Timestamp
//
class TimestampColumnType(row: ResultSetRow) extends IColumnType[Timestamp] {
  override def nextValueOption: Option[Timestamp] = row.nextTimestampOption
}
object TimestampColumnType extends ColumnTypeFactory[Timestamp] {
  def apply(row: ResultSetRow) = new TimestampColumnType(row)
}

//
// DateTime
//
class DateTimeColumnType(row: ResultSetRow) extends IColumnType[DateTime] {
  override def nextValueOption: Option[DateTime] = row.nextDateOption.map(d => new DateTime(d.getTime))
}
object DateTimeColumnType extends ColumnTypeFactory[DateTime] {
  def apply(row: ResultSetRow) = new DateTimeColumnType(row)
}

//
// Duration
//
class DurationColumnType(row: ResultSetRow) extends IColumnType[Duration] {
  override def nextValueOption: Option[Duration] = row.nextLongOption.map(new Duration(_))
}
object DurationColumnType extends ColumnTypeFactory[Duration] {
  def apply(row: ResultSetRow) = new DurationColumnType(row)
}
