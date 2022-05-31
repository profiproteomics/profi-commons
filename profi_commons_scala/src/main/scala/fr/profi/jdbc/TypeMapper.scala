package fr.profi.jdbc

object SupportedTypes extends Enumeration {
  val BOOLEAN, INTEGER, LONG, FLOAT, DOUBLE, BYTES, STRING, DATE, TIMESTAMP, DATETIME, DURATION = Value
}

trait ITypeMapper {  
  val booleanType: Any = SupportedTypes.BOOLEAN
  val integerType: Any = SupportedTypes.INTEGER
  val longType: Any = SupportedTypes.LONG
  val floatType: Any = SupportedTypes.FLOAT
  val doubleType: Any = SupportedTypes.DOUBLE
  val bytesType: Any = SupportedTypes.BYTES
  val stringType: Any = SupportedTypes.STRING
  val dateType: Any = SupportedTypes.DATE
  val timestampType: Any = SupportedTypes.TIMESTAMP
  val dateTimeType: Any = SupportedTypes.DATETIME
  val durationType: Any = SupportedTypes.DURATION
}

object DefaultTypeMapper extends ITypeMapper

object SQLiteTypeMapper extends ITypeMapper {
  override val booleanType = SupportedTypes.STRING
  override val dateType = SupportedTypes.STRING
  override val timestampType = SupportedTypes.STRING
  override val dateTimeType = SupportedTypes.STRING
  override val durationType = SupportedTypes.STRING
}
