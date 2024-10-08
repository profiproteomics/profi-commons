package fr.profi.jdbc

import org.joda.time.Duration

import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec
import org.scalatestplus.junit.JUnitRunner

import org.junit.runner.RunWith

import easy._

@RunWith(classOf[JUnitRunner])
class SQLDialectSpec extends AnyFunSpec with Matchers {

  describe("DefaultSQLDialect") {
    it("should combine the parameters with the query") {
      val expected = "insert into testtable( c1, c3, c4) values( 234, 'test', 3900000 )"
      val actual = DefaultSQLDialect.format(
        "insert into ?( c1, c3, c4) values( ?, ?, ? )",
        Identifier("testtable"), 234, "test", Duration.standardMinutes(65))

      actual should equal(expected)
    }
  }
  
  /*describe("SQLiteSQLDialect") {
    
    val dialect = SQLiteSQLDialect
    
    it("should format boolean as integers") {
      dialect.booleanFormatter.formatBoolean(true).isInstanceOf[Integer] should equal(true)
    }
    
  }*/
  
}
