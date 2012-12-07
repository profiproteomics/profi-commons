package fr.profi.jdbc

package object easy {

  import java.util.Date
  import java.sql.Timestamp
  
  import org.joda.time.DateTime
  import org.joda.time.Duration
  
  import fr.profi.jdbc.ResultSetRow
  
  // SQLFormattableImplicits
  implicit def null2Formattable(wrapped: Null) = NullFormattable( null )
  implicit def string2Formattable(wrapped: String) = StringFormattable(wrapped)
  implicit def boolean2Formattable(wrapped: Boolean) = BooleanFormattable(wrapped)
  implicit def int2Formattable(wrapped: Int) = IntFormattable(wrapped)
  implicit def long2Formattable(wrapped: Long) = LongFormattable(wrapped)  
  implicit def float2Formattable(wrapped: Float) = FloatFormattable(wrapped)
  implicit def double2Formattable(wrapped: Double) = DoubleFormattable(wrapped)
  implicit def date2Formattable(wrapped: Date) = DateTimeFormattable(wrapped)
  implicit def timestamp2Formattable(wrapped: Timestamp) = TimestampFormattable(wrapped)
  implicit def dateTime2Formattable(wrapped: DateTime) = DateTimeFormattable(wrapped)  
  implicit def duration2Formattable(wrapped: Duration) = DurationFormattable(wrapped)
  
  implicit def nullOption2Formattable( wrapped: Option[Null] ) = NullFormattable( null )
  implicit def stringOption2Formattable(wrapped: Option[String]) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => StringFormattable(value)
  }
  implicit def booleanOption2Formattable(wrapped: Option[Boolean]) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => BooleanFormattable(value)
  }
  implicit def intOption2Formattable( wrapped: Option[Int] ) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => IntFormattable(value)
  }
  implicit def longOption2Formattable(wrapped: Option[Long]) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => LongFormattable(value)
  }
  implicit def floatOption2Formattable(wrapped: Option[Float]) = wrapped match {
    case None => NullFormattable(null )
    case Some(value) => FloatFormattable(value)
  }
  implicit def doubleOption2Formattable(wrapped: Option[Double]) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => DoubleFormattable(value)
  }
  implicit def timestampOption2Formattable(wrapped: Option[Timestamp]) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => TimestampFormattable(value)
  }
  implicit def dateTimeOption2Formattable(wrapped: Option[DateTime]) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => DateTimeFormattable(value)
  }
  implicit def durationOption2Formattable(wrapped: Option[Duration]) = wrapped match {
    case None => NullFormattable( null )
    case Some(value) => DurationFormattable(value)
  }
  
  // ResultSetRowImplicits
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