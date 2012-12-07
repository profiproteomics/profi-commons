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
  def escaped(dialect: AbstractSQLDialect ): String
  
  /**
   * Used when doing batch inserts or updates. Should use
   * the given ReusableStatement to add the parameter.
   */
  def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect ): Unit
  
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
class NullComparable(val value: Option[ISQLFormattable]) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    value.map("=" + _.escaped(dialect)).getOrElse("is null")
  }
  
  @throws( classOf[Exception] )
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = {
    throw new Exception("incompatible with prepared statements")
  }
  
}
object NullComparable {
  def apply(value: Option[ISQLFormattable]) = new NullComparable(value)
}

/**
 * Wrap your optional value in Nullable to have it converted to null if None
 */
class Nullable(val value: Option[ISQLFormattable]) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    value.map(_.escaped(dialect)).getOrElse("null")
  }
  
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = statement.addNull
}
object Nullable {
  def apply(value: Option[ISQLFormattable]) = new Nullable(value)
}

class NullFormattable( override val value: Null ) extends Nullable( value ) {
    override def escaped( dialect: AbstractSQLDialect ): String = "null"
}
object NullFormattable{
    def apply( value: Null ) = new NullFormattable( value )
}

/**
 * Wrap a parameter string in an Identifier to avoid escaping.
 * An identifier may correspond to a database name or a table name.
 */
class Identifier(val value: String) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = value
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = statement.addString(value)
  
}
object Identifier {
  def apply(value: String) = new Identifier(value)
}

//
// String
//
class StringFormattable(val value: String) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = dialect.toSQLString(value)  
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = statement.addString(value)
  
}
object StringFormattable {
  def apply(value: String) = new StringFormattable(value)
}

//
// Boolean
// 
class BooleanFormattable(val value: Boolean) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    dialect.booleanFormatter.formatBoolean(value).toString
  }
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = {
    dialect.typeMapper.booleanType match {
      case SupportedTypes.BOOLEAN => statement.addBoolean(value)
      case SupportedTypes.STRING => statement.addString( this.escaped(dialect) )
    }
  }
  
}
object BooleanFormattable {
  def apply(value: Boolean) = new BooleanFormattable(value)
}

//
// Long
//
class LongFormattable(val value: Long) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = value.toString
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit =  statement.addLong(value)
  
}
object LongFormattable {
  def apply(value: Long) = new LongFormattable(value)
}

//
// Int
//
class IntFormattable(val value: Int) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = value.toString
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = statement.addInt(value)
  
}
object IntFormattable {
  def apply(value: Int) = new IntFormattable(value)
}

//
// Float
//
class FloatFormattable(val value: Float) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = "%f".formatLocal(java.util.Locale.UK,value)
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = statement.addFloat(value)
  
}
object FloatFormattable {
  def apply(value: Float) = new FloatFormattable(value)
}

//
// Double
//
class DoubleFormattable(val value: Double) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = "%f".formatLocal(java.util.Locale.UK,value)
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = statement.addDouble(value)
  
}
object DoubleFormattable {
  def apply(value: Double) = new DoubleFormattable(value)
}

//
// DateTime
//
class DateTimeFormattable(val value: DateTime) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    dialect.toSQLString( dialect.timeStampFormatter.print(value) )
  }
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = {
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
class TimestampFormattable(val value: Timestamp) extends ISQLFormattable {
  
  private def _toDateTime() = new DateTime( value.getTime() )
  
  override def escaped(dialect: AbstractSQLDialect): String = {
    dialect.toSQLString( dialect.timeStampFormatter.print( _toDateTime ) )
  }
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = {
    dialect.typeMapper.timestampType match {
      case SupportedTypes.TIMESTAMP =>  statement.addTimestamp(value)
      case SupportedTypes.STRING => statement.addString( dialect.timeStampFormatter.print( _toDateTime ) )
    }
  }
  
}
object TimestampFormattable {  
  def apply(value: Timestamp) = new TimestampFormattable( value )
}

//
// Duration
//
/**
 * Formats an Duration object by converting it to milliseconds.
 */
class DurationFormattable(val value: Duration) extends ISQLFormattable {
  
  override def escaped(dialect: AbstractSQLDialect): String = value.getMillis.toString
  
  override def addTo(statement: StatementWrapper, dialect: AbstractSQLDialect): Unit = {
    dialect.typeMapper.durationType match {
      case SupportedTypes.DURATION => statement.addLong( value.getMillis )
      case SupportedTypes.STRING => statement.addString( value.getMillis.toString )
    }
  }
  
}
object DurationFormattable {
  def apply(value: Duration) = new DurationFormattable(value)
}
