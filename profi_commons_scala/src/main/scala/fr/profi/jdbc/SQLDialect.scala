package fr.profi.jdbc

import java.util.Date
import java.sql.Timestamp

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.ISODateTimeFormat

/**
 * Currently a private class responsible for formatting SQL used in
 * transactions (@see Transaction). It does properly format standards
 * classes like DateTime, Floats, Longs and Integers as well as some
 * SQL specific classes like Nullable, NullComparable and Identifier.
 * See their documentation for more info on how to use them.
 */
abstract class AbstractSQLDialect(
  val timeStampFormatter: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSS"),
  val booleanFormatter: IBooleanFormatter = DefaultBooleanFormatter,
  val typeMapper: ITypeMapper = DefaultTypeMapper,
  val generateKeyParam: Any = 1,
  val inExpressionCountLimit: Int = 50000,
  val fetchSize: Option[Int] = None
) {
  
  var quotingChar ='\''

  def format(sql: String, params: ISQLFormattable*): String = formatSeq(sql, params.toArray )

  def formatSeq(sql: String, params: Array[ISQLFormattable] ): String = {
    sql.replace("?", "%s").format( params.map(p => p.escaped(this) ): _* )
  }

  /**
   * Escapes  "'" and "\" in the string for use in a SQL query
   */
  def escapeString(str: String): String = str.replace("'", "''").replace("\\", "\\\\")
  
  /**
   * Quotes the passed string according to the defined sqlQuote
   */
  def quoteString(str: String): String = {
    val sb = new StringBuilder
    sb.append(quotingChar).append(str).append(quotingChar)
    sb.toString
  }

  /**
   * Escapes and quotes the given string
   */
  def toSQLString(str: String): String = quoteString(escapeString(str))
}


object DefaultSQLDialect extends AbstractSQLDialect
/*(
  DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSS"), //  ISODateTimeFormat.dateTimeNoMillis,
  DefaultBooleanFormatter,
  DefaultTypeMapper
)

object HSQLDBDialect extends AbstractSQLDialect

object H2Dialect extends AbstractSQLDialect

object PgDialect extends AbstractSQLDialect*/

object SQLiteSQLDialect extends AbstractSQLDialect(
  DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSS"),
  DefaultBooleanFormatter,
  SQLiteTypeMapper,
  "last_insert_rowid()",
  999
)
