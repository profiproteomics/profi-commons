package fr.profi.jdbc

import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet

import scala.collection.mutable.ArrayBuffer

import org.joda.time.DateTime
import org.joda.time.Duration

import fr.profi.jdbc.RichConnection.conn2RichConn
import fr.profi.jdbc.ResultSetRowImplicits._

/**
 * Performs queries on a given java.sql.Connection object using the
 * formatting rules defined in a provided AbstractSQLFormatter.
 * 
 * @throws SQLException all methods executing queries will throw SQLException
 *         if the query was not properly formatted or something went wrong in
 *         the database during execution.
 *
 * @throws IllegalFormatException: Will be throw by all method if the format
 *         string is invalid or if there is not enough parameters.
 */
trait SQLQueryExecution {
  
  val connection: Connection
  val dialect: AbstractSQLDialect

  /**
   * Returns all records returned by the query after being converted by the
   * given block. All objects are kept in memory to this method is no suited
   * for very big result sets. Use selectAndProcess if you need to process
   * bigger datasets.
   *
   * @param sql query that should return records
   * @param params are the optional parameters used in the query
   * @param block is a function converting the row to something else
   */
  def select[T](sql: String, params: ISQLFormattable*)(block: ResultSetRow => T): Seq[T] = {
    val results = new ArrayBuffer[T]
    _selectIntoBuffer(Some(results), sql, params.toSeq)(block)
    results
  }

  /**
   * Executes the query and passes each row to the given block. This method
   * does not keep the objects in memory and returns Unit so the row needs to
   * be fully processed in the block.
   *
   * @param sql query that should return records
   * @param params are the optional parameters used in the query
   * @param block is a function fully processing each row
   */
  def selectAndProcess(sql: String, params: ISQLFormattable*)(block: ResultSetRow => Unit): Unit = {
    _selectIntoBuffer(None, sql, params.toSeq)(block)
  }

  /**
   * Returns the first record returned by the query after being converted by the
   * given block. If the query does not return anything None is returned.
   *
   * @param sql query that should return records
   * @param params are the optional parameters used in the query
   * @param block is a function converting the row to something else
   */
  def selectHeadOption[T](sql: String, params: ISQLFormattable*)(block: ResultSetRow => T): Option[T] = {
    select(sql, params.toSeq: _*)(block).headOption
  }

  /**
   * Return the head record from a query that must be guaranteed to return at least one record.
   * The query may return more records but those will be ignored.
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @param block is a function converting the returned row to something useful.
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectHead[T](sql: String, params: ISQLFormattable*)(block: ResultSetRow => T): T = {
    select(sql, params.toSeq: _*)(block).head
  }

  /**
   * Convenience method for interpreting the first column of the first record as a long
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a long
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectLong(sql: String, params: ISQLFormattable*): Long = {
    selectHead(sql, params.toSeq: _*)(row2Long)
  }

  /**
   * Convenience method for interpreting the first column of the first record as a Int
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a Int
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectInt(sql: String, params: ISQLFormattable*): Int = {
    selectHead(sql, params.toSeq: _*)(row2Int)
  }

  /**
   * Convenience method for interpreting the first column of the first record as a Boolean
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a Boolean
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectBoolean(sql: String, params: ISQLFormattable*): Boolean = {
    selectHead(sql, params.toSeq: _*)(row2Boolean)
  }

  /**
   * Convenience method for interpreting the first column of the first record as a String
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a String
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectString(sql: String, params: ISQLFormattable*): String = {
    selectHead(sql, params.toSeq: _*)(row2String)
  }

  /**
   * Convenience method for interpreting the first column of the first record as a Float
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a Float
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectFloat(sql: String, params: ISQLFormattable*): Float = {
    selectHead(sql, params.toSeq: _*)(row2Float)
  }

  /**
   * Convenience method for interpreting the first column of the first record as a Double
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a Double
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectDouble(sql: String, params: ISQLFormattable*): Double = {
    selectHead(sql, params.toSeq: _*)(row2Double)
  }

  /**
   * Convenience method for interpreting the first column of the first record as a DateTime
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a DateTime
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectDateTime(sql: String, params: ISQLFormattable*): DateTime = {
    selectHead(sql, params.toSeq: _*)(row2DateTime)
  }
  
  /**
   * Convenience method for interpreting the first column of the first record as a Timestamp
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a DateTime
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectTimestamp(sql: String, params: ISQLFormattable*): java.sql.Timestamp = {
    selectHead(sql, params.toSeq: _*)(row2Timestamp)
  }

  /**
   * Convenience method for interpreting the first column of the first record as a Duration
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be intepreted as a Duration
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectDuration(sql: String, params: ISQLFormattable*): Duration = {
    selectHead(sql, params.toSeq: _*)(row2Duration)
  }

  /**
   * Executes the given query and returns the number of affected records
   *
   * @param sql query that must not return any records
   * @param params are the optional parameters used in the query
   * @return the number of affected records
   */
  def execute(sql: String, params: ISQLFormattable*): Int = {
    connection.usingStatement { statement =>
      val sqlString = dialect.formatSeq(sql, params.toSeq)
      statement.executeUpdate( sqlString )
    }
  }

  /**
   * Will pass a ReusableStatement to the given block. This block
   * may add parameters to the statement and execute it multiple times.
   * The statement will be automatically closed onced the block returns.
   *
   * Example:
   *     tx.executeBatch( "insert into foo values(?)" ) { statement =>
   *         items.foreach { statement.executeWith( _ ) }
   *     }
   *
   * @return the result of the block
   * @throws SQLException if the query is missing parameters when executed
   *         or if they are of the wrong type.
   */
  def executePrepared[T](sql: String, generateKeys: Boolean = false)(block: (PreparedStatementWrapper) => T): T = {
    connection.usingPreparedStatement(sql, dialect, generateKeys)(block)
  }
  
  def executeBatch[T](sql: String)(block: (BatchStatementWrapper) => T): Array[Int] = {
    connection.usingBatch(sql, dialect)(block)    
  }

  private def _selectIntoBuffer[T](
                buffer: Option[ArrayBuffer[T]],
                sql: String, params: Seq[ISQLFormattable]) (block: (ResultSetRow) => T): Unit = {
                
    connection.usingStatement { statement =>
      val rs = statement.executeQuery(dialect.formatSeq(sql, params))
      val append = buffer.isDefined

      while (rs.next) {
        val value = block(ResultSetRow(rs))
        if (append) buffer.get.append(value)
      }
    }
  }
  
}
