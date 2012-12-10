package fr.profi.jdbc

import java.util.Date
import java.sql.SQLException

import org.scalatest.FunSpec
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.BeforeAndAfterEach

import org.joda.time.DateTime
import org.joda.time.Duration
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import easy._

@RunWith(classOf[JUnitRunner])
class ResultSetRowSpec extends FunSpec with ShouldMatchers with BeforeAndAfterEach {

  val eDBC = eDbcH2TestInstance
  
  describe("ResultSetRow") {

    it("should return the column names of the row") {
      eDBC.inTx { tx =>
        val expectedColumns = Array("id", "name")
        tx.execute("create table resultsetrowspec(id int, name varchar(265))")
        tx.execute("insert into resultsetrowspec values(?, ?)", 242, "test1")
        tx.select("select id, name from resultsetrowspec") { row =>
          row.columnNames should equal(expectedColumns)
          row.nextString
        }
      }
    }

    it("should return a Boolean") {
      eDBC.inTx { tx =>
        val value1 = Some(false)
        val value2 = None
        tx.execute("create table boolean_table(c1 boolean, c2 boolean)")
        tx.execute("insert into boolean_table values(?, null)", value1.get)
        tx.select("select c1, c2 from boolean_table") { row =>
          row.nextBooleanOption should equal(value1)
          row.nextBooleanOption should equal(value2)
        }
      }
    }

    it("should return a Long") {
      eDBC.inTx { tx =>
        val value1 = Some(12345L)
        val value2 = None
        tx.execute("create table long_table(c1 bigint, c2 bigint)")
        tx.execute("insert into long_table values(?, null)", value1.get)
        tx.select("select c1, c2 from long_table") { row =>
          row.nextLongOption should equal(value1)
          row.nextLongOption should equal(value2)
        }
      }
    }

    it("should return an Int") {
      eDBC.inTx { tx =>
        val value1 = Some(12345)
        val value2 = None
        tx.execute("create table int_table(c1 int, c2 int)")
        tx.execute("insert into int_table values(?, null)", value1.get)
        tx.select("select c1, c2 from int_table") { row =>
          row.nextIntOption should equal(value1)
          row.nextIntOption should equal(value2)
        }
      }
    }

    it("should return a String") {
      eDBC.inTx { tx =>
        val value1 = Some("test")
        val value2 = None
        tx.execute("create table string_table(c1 varchar(256), c2 varchar(16))")
        tx.execute("insert into string_table values(?, null)", value1.get)
        tx.select("select c1, c2 from string_table") { row =>
          row.nextStringOption should equal(value1)
          row.nextStringOption should equal(value2)
        }
      }
    }

    it("should return a Date") {
      eDBC.inTx { tx =>
        val value1 = Some(new Date)
        val value2 = None
        tx.execute("create table date_table(c1 timestamp, c2 timestamp)")
        tx.execute("insert into date_table values(?, null)", value1.get)
        tx.select("select c1, c2 from date_table") { row =>
          row.nextDate.getTime should equal(value1.get.getTime)
          row.nextDateOption should equal(value2)
        }
      }
    }

    it("should return a Float") {
      eDBC.inTx { tx =>
        val value1 = Some(1.5f)
        val value2 = None
        tx.execute("create table float_table(c1 real, c2 real)")
        tx.execute("insert into float_table values(?, null)", value1.get)
        tx.select("select c1, c2 from float_table") { row =>
          row.nextFloatOption should equal(value1)
          row.nextFloatOption should equal(value2)
        }
      }
    }

    it("should return a Double") {
      eDBC.inTx { tx =>
        val value1 = Some(3274832748932743.45)
        val value2 = None
        tx.execute("create table double_table(c1 double, c2 double)")
        tx.execute("insert into double_table values(?, null)", value1.get)
        tx.select("select c1, c2 from double_table") { row =>
          row.nextDoubleOption should equal(value1)
          row.nextDoubleOption should equal(value2)
        }
      }
    }
  }
}
