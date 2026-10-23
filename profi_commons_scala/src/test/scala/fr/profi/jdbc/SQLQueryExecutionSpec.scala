package fr.profi.jdbc

import java.sql.SQLException

import fr.profi.jdbc.easy._
import org.junit.runner.RunWith
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalatestplus.junit.JUnitRunner

import scala.collection.mutable.ArrayBuffer
import scala.language.postfixOps


@RunWith(classOf[JUnitRunner])
class SQLQueryExecutionSpec extends AnyFunSpec with Matchers with BeforeAndAfterEach {
  
  val eDBC = eDbcH2TestInstance
  
  override def beforeEach() = eDBC.inTx { tx =>
    tx.execute("create table transactionspec(id int, name varchar(265))")
    tx.execute("insert into transactionspec values(?, ?)", 242, "test1")
    tx.execute("insert into transactionspec values(?, ?)", 23, "test2")
    tx.execute("insert into transactionspec values(?, ?)", 42, "test3")
  }

  override def afterEach() = eDBC.inTx { tx =>
    tx.execute("drop table transactionspec")
  }

  describe("SQLQueryExecution") {

    describe("select") {

      it("should return a Seq of the records converted by block") {
        eDBC.inTx { tx =>
          val expected = Seq("test1", "test2", "test3")
          val actual = tx.select("select name from transactionspec") { row =>
            row.nextString
          }

          actual should equal(expected)
        }
      }

      it("should return an empty Seq if no records were found") {
        eDBC.inTx { tx =>
          val actual = tx.select(
            """select name from transactionspec 
                            where id > 1000
                        """) { _.nextString }

          actual should be('empty)
        }
      }
    }

    describe("selectHeadOption") {

      it("should return the first record if one or more is returned by query") {
        eDBC.inTx { tx =>
          val expected = Some("test1")
          val actual = tx.selectHeadOption("select name from transactionspec") { row =>
            row.nextString
          }

          actual should equal(expected)
        }
      }

      it("should return None if the query did not return any records") {
        eDBC.inTx { tx =>
          val expected = None
          val actual = tx.selectHeadOption(
            """select name from transactionspec 
                            where id > 1000
                        """) { _.nextString }

          actual should equal(expected)
        }
      }
    }

    describe("selectHead") {

      it("should return the first column of the first record") {
        eDBC.inTx { tx =>
          val expected = 242L
          val actual = tx.selectHead("select id from transactionspec")(row2Long)

          actual should equal(expected)
        }
      }

      it("should throw a NoSuchElementException if no record was returned") {
        eDBC.inTx { tx =>
          intercept[NoSuchElementException] {
            tx.selectHead(
              """select id from transactionspec 
                                where id > 1000
                            """)(row2Long)
          }
        }
      }
    }

    describe("selectLong") {

      it("should return the first column of the first records as a Long") {
        eDBC.inTx { tx =>
          val expected = 242L
          val actual = tx.selectLong("select id from transactionspec")

          actual should equal(expected)
        }
      }

      it("should throw an SQLException if the value is not a Long") {
        eDBC.inTx { tx =>
          intercept[SQLException] {
            tx.selectLong("select 'nan' from transactionspec")
          }
        }
      }

      it("should throw a NoSuchElementException if no record was returned") {
        eDBC.inTx { tx =>
          intercept[NoSuchElementException] {
            tx.selectLong("select id from transactionspec where id > 1000")
          }
        }
      }
    }

    describe("executePrepared") {
      
      it("should return the number of inserted records") {
        case class Item(v1: Long, v2: String)
        val items = Seq(Item(1, "test"), Item(1, "test"))
        val count = eDBC.inTx { tx =>
          tx.executePrepared("insert into transactionspec values(?, ?)") { statement =>
            var counter = 0
            items.foreach { item =>
              counter += (statement << item.v1 << item.v2 <<!)
            }
            counter
          }
        }

        count should equal(items.size)
      }

      it("should return the number of updated records") {
        val itemsToUpdate = Seq(23, 42, 38, 232)
        val existingItems = Seq(23, 42)
        val count = eDBC.inTx { tx =>
          tx.executePrepared("update transactionspec set name='foo' where id=?") { statement =>
            var counter = 0
            itemsToUpdate.foreach { item =>
              counter += (statement << item <<!)
            }
            counter
          }
        }

        count should equal(existingItems.size)
      }

      it("should prepare the statement only once (unlike normal execute-calls)") {
        case class Item(v1: Long, v2: String)

        // Spy connection: counts the JDBC statement factory calls and delegates everything else
        val prepareCount = new java.util.concurrent.atomic.AtomicInteger(0)
        val createCount = new java.util.concurrent.atomic.AtomicInteger(0)
        val realConnection = eDBC.connection
        val spyConnection = java.lang.reflect.Proxy.newProxyInstance(
          classOf[java.sql.Connection].getClassLoader,
          Array[Class[_]](classOf[java.sql.Connection]),
          new java.lang.reflect.InvocationHandler {
            def invoke(proxy: AnyRef, method: java.lang.reflect.Method, args: Array[AnyRef]): AnyRef = {
              method.getName match {
                case "prepareStatement" => prepareCount.incrementAndGet()
                case "createStatement" => createCount.incrementAndGet()
                case _ =>
              }
              method.invoke(realConnection, args: _*)
            }
          }
        ).asInstanceOf[java.sql.Connection]
        val spiedExecutor = EasyDBC(spyConnection, eDBC.dialect, eDBC.txIsolationLevel)

        val size = 1000
        val items = (0 until size).map(i => Item(i, "foo" + i))
        val sql = "insert into transactionspec values(?, ?)"

        spiedExecutor.inTx { tx =>
          prepareCount.set(0); createCount.set(0)
          items.foreach { item => tx.execute(sql, item.v1, item.v2) }
          val (normalPrepared, normalCreated) = (prepareCount.get, createCount.get)

          prepareCount.set(0); createCount.set(0)
          tx.executePrepared(sql) { statement =>
            items.foreach { item => statement << item.v1 << item.v2 <<! }
          }
          val (batchPrepared, batchCreated) = (prepareCount.get, createCount.get)

          // normal execute: one Statement per item, no PreparedStatement
          normalCreated should equal(size)
          normalPrepared should equal(0)

          // executePrepared: a single PreparedStatement reused for every item
          batchPrepared should equal(1)
          batchCreated should equal(0)

          tx.selectLong("select count(*) from transactionspec where name like 'foo%'") should equal(2L * size)
        }
      }

    }
  }
}