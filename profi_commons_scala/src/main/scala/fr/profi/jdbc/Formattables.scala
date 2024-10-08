package fr.profi.jdbc

import java.sql.Timestamp
import java.util.Date

import org.joda.time.{DateTime, Duration}

object JdbcConstant {
  val NULL_STRING = "null"
}

/**
 * The base for any parameter used in a sql query. jdbc comes
 * with quite a few supported types but it's easy to extend the
 * support for custom types if needed by implementing this interface
 *
 * @see Formattables.scala for examples on implementations.
 * @see AbstractSQLDialect for escaping and quoting of strings.
 */
sealed trait ISQLFormattable extends Any {
  
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
final case class NullComparable(val value: Option[ISQLFormattable]) extends AnyVal with ISQLFormattable {
  
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
final case class Nullable(val value: Option[ISQLFormattable]) extends AnyVal with ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    value.map(_.escaped(dialect)).getOrElse(JdbcConstant.NULL_STRING)
  }
  
  override def addTo(statement: StatementWrapper): Unit = statement.addNull
}

final case class NullFormattable( val value: Null ) extends AnyVal with ISQLFormattable {
  override def escaped( dialect: AbstractSQLDialect ): String = JdbcConstant.NULL_STRING
  override def addTo(statement: StatementWrapper): Unit = statement.addNull
}
/*object NullFormattable {
  def apply(value: Null) = new NullFormattable(value)  
}*/

/**
 * Wrap a parameter string in an Identifier to avoid escaping.
 * An identifier may correspond to a database name or a table name.
 */
final case class Identifier(val value: String) extends AnyVal with ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = value
  override def addTo(statement: StatementWrapper): Unit = statement.addString(value)
}

//
// String
//
final case class StringFormattable(val value: String) extends AnyVal with ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = dialect.toSQLString(value)  
  override def addTo(statement: StatementWrapper): Unit = statement.addString(value)
}

//
// Boolean
// 
final case class BooleanFormattable(val value: Boolean) extends AnyVal with ISQLFormattable {
  
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
final case class LongFormattable(val value: Long) extends AnyVal with ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = value.toString
  override def addTo(statement: StatementWrapper): Unit =  statement.addLong(value)
}

//
// Int
//
final case class IntFormattable(val value: Int) extends AnyVal with ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = value.toString
  override def addTo(statement: StatementWrapper): Unit = statement.addInt(value)
}

//
// Float
//
final case class FloatFormattable(val value: Float) extends AnyVal with ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = "%f".formatLocal(java.util.Locale.UK,value)
  override def addTo(statement: StatementWrapper): Unit = statement.addFloat(value)
}

//
// Double
//
final case class DoubleFormattable(val value: Double) extends AnyVal with ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = "%f".formatLocal(java.util.Locale.UK,value)
  override def addTo(statement: StatementWrapper): Unit = statement.addDouble(value)
}

//
// Bytes
//
final case class BytesFormattable(val value: Array[Byte]) extends AnyVal with ISQLFormattable {
  override def escaped(dialect: AbstractSQLDialect): String = throw new Exception("NYI")
  override def addTo(statement: StatementWrapper): Unit = statement.addBytes(value)
}

//
// DateTime
//
final case class DateTimeFormattable(val value: DateTime) extends AnyVal with ISQLFormattable {
  
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
  //def apply(value: DateTime) = new DateTimeFormattable(value)  
  def apply(value: Date) = new DateTimeFormattable( new DateTime(value) )
}

//
// Timestamp
//
final case class TimestampFormattable(val value: Timestamp) extends AnyVal with ISQLFormattable {
  
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
final case class DurationFormattable(val value: Duration) extends AnyVal with ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = value.getMillis.toString
  
  override def addTo(statement: StatementWrapper): Unit = {
    statement.dialect.typeMapper.durationType match {
      case SupportedTypes.DURATION => statement.addLong( value.getMillis )
      case SupportedTypes.STRING => statement.addString( value.getMillis.toString )
    }
  }
  
}
