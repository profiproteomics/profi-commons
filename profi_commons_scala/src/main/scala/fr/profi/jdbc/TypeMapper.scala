package fr.profi.jdbc

import java.sql.Date
import java.sql.Timestamp

import org.joda.time.DateTime
import org.joda.time.Duration

object SupportedTypes extends Enumeration {
  val STRING, BOOLEAN, LONG, INT, FLOAT, DOUBLE, DATE, TIMESTAMP, DATETIME, DURATION = Value
}

trait ITypeMapper {
  val stringType: Any = SupportedTypes.STRING
  val booleanType: Any = SupportedTypes.BOOLEAN
  val longType: Any = SupportedTypes.LONG
  val floatType: Any = SupportedTypes.FLOAT
  val doubleType: Any = SupportedTypes.DOUBLE
  val dateType: Any = SupportedTypes.DATE
  val timestampType: Any = SupportedTypes.TIMESTAMP
  val dateTimeType: Any = SupportedTypes.DATETIME
  val durationTimeType: Any = SupportedTypes.DURATION
}

object DefaultTypeMapper extends ITypeMapper

object SQLiteTypeMapper extends ITypeMapper {
  override val booleanType = SupportedTypes.STRING
  override val dateType = SupportedTypes.STRING
  override val timestampType = SupportedTypes.STRING
  override val dateTimeType = SupportedTypes.STRING
  override val durationTimeType = SupportedTypes.STRING
}
