package fr.profi.jdbc

import fr.profi.jdbc.easy._
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.matchers.should.Matchers
import org.scalatest.funspec.AnyFunSpec
import org.scalatestplus.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class InTransactionSpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {

  val eDBC = eDbcH2TestInstance
  
  override def beforeEach() {
    eDBC.execute("CREATE TABLE in_transaction_spec(id int, name varchar(265))")
  }

  override def afterEach() = {
    eDBC.execute("DROP TABLE in_transaction_spec")
  }
  
  val insertQuery = "INSERT INTO in_transaction_spec VALUES (?, ?)"
  val insertQueryParams: List[ISQLFormattable] = List( 123, "test" )
  val countQuery = "SELECT count(*) FROM in_transaction_spec"

  describe("EasyDBC.inTx") {

    it("should commit after block has been executed") {
      eDBC.inTx { tx =>
          tx.execute( insertQuery, insertQueryParams: _* )
        }
  
      eDBC.inTx { tx =>
        tx.selectLong(countQuery) should be(1)
      }
    }

    it("should rollback if asked to do so") {
      eDBC.inTx { tx =>
        tx.execute(insertQuery, insertQueryParams: _*)
        tx.rollback()
      }

      eDBC.inTx { tx =>
        tx.selectLong(countQuery) should be(0)
      }
    }

    it("should rollback if an exception is thrown") {

      intercept[Exception] {
        eDBC.inTx { tx =>
          tx.execute(insertQuery, insertQueryParams: _*)
          throw new Exception("oh no")
        }
      }

      eDBC.inTx { tx =>
        tx.selectLong(countQuery) should be(0)
      }

    }

    it("should leave the connection opened after execution finished successfully") {

      eDBC.inTx { tx =>
        tx.execute(insertQuery, insertQueryParams: _*)
      }

      eDBC.connection.isClosed should be(false)
    }

    it("should leave the connection opened after execution failed") {

      eDBC.inTx { tx =>
        intercept[Exception] {
          tx.execute(insertQuery, insertQueryParams: _*)
          throw new Exception("oh no")
        }
      }
      eDBC.connection.isClosed should be(false)
    }

  }
  
  
}
