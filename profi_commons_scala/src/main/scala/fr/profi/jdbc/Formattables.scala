package fr.profi.jdbc

import java.util.Date
import java.sql.Timestamp

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormatter


/**
 * The base for any parameter used in a sql query. jdbc comes
 * with quite a few supported types but it's easy to extend the
 * support for custom types if needed by implementing this interface
 *
 * @see Formattables.scala for examples on implementations.
 * @see AbstractSQLDialect for escaping and quoting of strings.
 */
trait ISQLFormattable {
  
  /**
   * Must return a sql escaped string of the parameter
   */
  def escaped(dialect: AbstractSQLDialect): String
  
  /**
   * Used when doing batch inserts or updates. Should use
   * the given ReusableStatement to add the parameter.
   */
  def addTo(statement: StatementWrapper): Unit
  
  /**
   * Should return the parameter as it is
   */
  def value(): Any
}

/**
 * Wrap your optional value in NullComparable to compare with null if None.
 *
 * Note: The '=' operator is added during formatting so don't include it in your SQL
 */
case class NullComparable(val value: Option[ISQLFormattable]) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    value.map("=" + _.escaped(dialect)).getOrElse("is null")
  }
  
  @throws( classOf[Exception] )
  override def addTo(statement: StatementWrapper): Unit = {
    throw new Exception("incompatible with prepared statements")
  }
  
}

/**
 * Wrap your optional value in Nullable to have it converted to null if None
 */
case class Nullable(val value: Option[ISQLFormattable]) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    value.map(_.escaped(dialect)).getOrElse("null")
  }
  
  override def addTo(statement: StatementWrapper): Unit = statement.addNull
}

class NullFormattable( override val value: Null ) extends Nullable( value ) {
  override def escaped( dialect: AbstractSQLDialect ): String = "null"
}
object NullFormattable {  
  def apply(value: Null) = new NullFormattable(value)  
}

/**
 * Wrap a parameter string in an Identifier to avoid escaping.
 * An identifier may correspond to a database name or a table name.
 */
case class Identifier(val value: String) extends ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = value
  override def addTo(statement: StatementWrapper): Unit = statement.addString(value)
}

//
// String
//
case class StringFormattable(val value: String) extends ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = dialect.toSQLString(value)  
  override def addTo(statement: StatementWrapper): Unit = statement.addString(value)
}

//
// Boolean
// 
case class BooleanFormattable(val value: Boolean) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    val formattedBool = dialect.booleanFormatter.formatBoolean(value)
    formattedBool match {
      case strBool: String => dialect.quoteString( strBool )
      case _ => formattedBool.toString
    }
  }
  override def addTo(statement: StatementWrapper): Unit = {
    val dialect  = statement.dialect
    dialect.typeMapper.booleanType match {
      case SupportedTypes.BOOLEAN => statement.addBoolean(value)
      case SupportedTypes.INTEGER => statement.addBoolean(value)
      case SupportedTypes.STRING => statement.addString(
        dialect.booleanFormatter.formatBoolean(value).toString
      )
    }
  }
  
}

//
// Long
//
case class LongFormattable(val value: Long) extends ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = value.toString
  override def addTo(statement: StatementWrapper): Unit =  statement.addLong(value)
}

//
// Int
//
case class IntFormattable(val value: Int) extends ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = value.toString
  override def addTo(statement: StatementWrapper): Unit = statement.addInt(value)
}

//
// Float
//
case class FloatFormattable(val value: Float) extends ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = "%f".formatLocal(java.util.Locale.UK,value)
  override def addTo(statement: StatementWrapper): Unit = statement.addFloat(value)
}

//
// Double
//
case class DoubleFormattable(val value: Double) extends ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = "%f".formatLocal(java.util.Locale.UK,value)
  override def addTo(statement: StatementWrapper): Unit = statement.addDouble(value)
}

//
// Bytes
//
case class BytesFormattable(val value: Array[Byte]) extends ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = throw new Exception("NYI")
  override def addTo(statement: StatementWrapper): Unit = statement.addBytes(value)
}

//
// DateTime
//
class DateTimeFormattable(val value: DateTime) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    dialect.toSQLString( dialect.timeStampFormatter.print(value) )
  }
  override def addTo(statement: StatementWrapper): Unit = {
    val dialect = statement.dialect
    dialect.typeMapper.dateTimeType match {
      case SupportedTypes.DATETIME => statement.addDateTime(value)
      case SupportedTypes.STRING => statement.addString( dialect.timeStampFormatter.print(value) )
    }
  }
  
}
object DateTimeFormattable {  
  def apply(value: DateTime) = new DateTimeFormattable(value)  
  def apply(value: Date) = new DateTimeFormattable( new DateTime(value) )
}

//
// Timestamp
//
case class TimestampFormattable(val value: Timestamp) extends ISQLFormattable {
  
  private def _toDateTime() = new DateTime( value.getTime() )
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    dialect.toSQLString( dialect.timeStampFormatter.print( _toDateTime ) )
  }
  override def addTo(statement: StatementWrapper): Unit = {
    val dialect = statement.dialect
    dialect.typeMapper.timestampType match {
      case SupportedTypes.TIMESTAMP =>  statement.addTimestamp(value)
      case SupportedTypes.STRING => statement.addString( dialect.timeStampFormatter.print( _toDateTime ) )
    }
  }
  
}

//
// Duration
//
/**
 * Formats an Duration object by converting it to milliseconds.
 */
case class DurationFormattable(val value: Duration) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = value.getMillis.toString
  
  override def addTo(statement: StatementWrapper): Unit = {
    statement.dialect.typeMapper.durationType match {
      case SupportedTypes.DURATION => statement.addLong( value.getMillis )
      case SupportedTypes.STRING => statement.addString( value.getMillis.toString )
    }
  }
  
}
