package fr.profi.jdbc

import org.joda.time.DateTime
import org.joda.time.Duration
import org.junit.runner.RunWith
import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec
import org.scalatestplus.junit.JUnitRunner

trait ColumnTypeSpec[T] extends AnyFunSpec with Matchers {

  val testIdentifier = columnTypeFactory.getClass.getSimpleName.replace("$", "")

  def sqlType: String
  def sqlFormattable: ISQLFormattable
  def columnTypeFactory: ColumnTypeFactory[T]

  val eDBC = eDbcH2TestInstance
  
  describe(testIdentifier) {

    it("should handle both defined and undefined value") {
      
      eDBC.inTx { tx =>

        // Using the test identifier as table since tables in hsqldb are not
        // transaction safe objects.
        val table = testIdentifier

        tx.execute(
          "CREATE TABLE ? (c1 ?, c2 ?)",
          Identifier(table), Identifier(sqlType), Identifier(sqlType)
        )
        
        tx.execute("INSERT INTO ? VALUES (?, null)", Identifier(table), sqlFormattable)

        tx.select("SELECT c1, c2 FROM ?", Identifier(table) ) { row =>
          columnTypeFactory(row).nextValue should equal(sqlFormattable.value)
          columnTypeFactory(row).nextValueOption should be(None)
        }
        
        tx.rollbackTransaction()
      }
    }
  }
}

@RunWith(classOf[JUnitRunner])
class TimestampColumnTypeSpec extends ColumnTypeSpec[java.sql.Timestamp] {
  def sqlType = "timestamp"
  val sqlFormattable = TimestampFormattable( new java.sql.Timestamp(new java.util.Date().getTime) )
  def columnTypeFactory = TimestampColumnType
}

@RunWith(classOf[JUnitRunner])
class DateTimeColumnTypeSpec extends ColumnTypeSpec[DateTime] {
  def sqlType = "timestamp"
  val sqlFormattable = DateTimeFormattable(new DateTime)
  def columnTypeFactory = DateTimeColumnType
}

@RunWith(classOf[JUnitRunner])
class DurationColumnTypeSpec extends ColumnTypeSpec[Duration] {
  def sqlType = "bigint"
  val sqlFormattable = DurationFormattable(Duration.standardHours(24))
  def columnTypeFactory = DurationColumnType
}

@RunWith(classOf[JUnitRunner])
class StringColumnTypeSpec extends ColumnTypeSpec[String] {
  def sqlType = "varchar(256)"
  val sqlFormattable = StringFormattable("hello this is a simple string")
  def columnTypeFactory = StringColumnType
}

@RunWith(classOf[JUnitRunner])
class IntColumnTypeSpec extends ColumnTypeSpec[Int] {
  def sqlType = "int"
  val sqlFormattable = IntFormattable(42)
  def columnTypeFactory = IntColumnType
}

@RunWith(classOf[JUnitRunner])
class LongColumnTypeSpec extends ColumnTypeSpec[Long] {
  def sqlType = "bigint"
  val sqlFormattable = LongFormattable(498902382837L)
  def columnTypeFactory = LongColumnType
}

@RunWith(classOf[JUnitRunner])
class BooleanColumnTypeSpec extends ColumnTypeSpec[Boolean] {
  def sqlType = "boolean"
  val sqlFormattable = BooleanFormattable(true)
  def columnTypeFactory = BooleanColumnType
}

@RunWith(classOf[JUnitRunner])
class DoubleColumnTypeSpec extends ColumnTypeSpec[Double] {
  def sqlType = "double"
  val sqlFormattable = DoubleFormattable(2454354.2737)
  def columnTypeFactory = DoubleColumnType
}

@RunWith(classOf[JUnitRunner])
class FloatColumnTypeSpec extends ColumnTypeSpec[Float] {
  def sqlType = "real"
  val sqlFormattable = FloatFormattable(2.42f)
  def columnTypeFactory = FloatColumnType
}