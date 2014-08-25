package fr.profi.jdbc

import java.sql.Connection
import java.sql.Statement
import java.sql.ResultSet
import java.sql.Statement.RETURN_GENERATED_KEYS
import java.sql.Statement.NO_GENERATED_KEYS

import scala.collection.mutable.ArrayBuffer

import org.joda.time.DateTime
import org.joda.time.Duration

import easy._
import fr.profi.util.primitives.AnyMap

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
  
  def getInExpressionCountLimit = dialect.inExpressionCountLimit

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
    _selectIntoBuffer(Some(results), sql, params.toArray )(block)
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
    _selectIntoBuffer(None, sql, params.toArray)(block)
  }
  
  def selectAllRecordsAsMaps( sql: String ): Array[Map[String,Any]] = {
    var colNames: Array[String] = null
    val records = Array.newBuilder[Map[String,Any]]
    
    // Execute SQL query to load records
    this.selectAndProcess( sql ) { r => 
      
      // Retrieve column names
      if( colNames == null ) { colNames = r.columnNames }
      
      // Build and append the record
      records += colNames.map( colName => ( colName -> r.nextAnyRefOrElse(null) ) ).toMap
    }
    
    records.result()
  }
  
  def selectAllRecords( sql: String ): Array[AnyMap] = {
    
    var colNames: Array[String] = null
    val records = Array.newBuilder[AnyMap]
    
    // Execute SQL query to load records
    this.selectAndProcess( sql ) { r => 
      
      // Retrieve column names
      if( colNames == null ) { colNames = r.columnNames }
      
      // Build the record
      val record = new AnyMap()
      for( colName <- colNames ) {
        record(colName) = r.nextAnyRefOrElse(null)
      }
      
      // Append the record
      records += record
    }
    
    records.result()
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
    this._usingStatement { statement =>
      val rs = statement.executeQuery(dialect.formatSeq(sql, params.toArray))
      if( rs.next ) Some(block(ResultSetRow(rs)))
      else None
    }
  }
  
  def selectHeadOrElse[T](sql: String, params: ISQLFormattable*)(block: ResultSetRow => T, default: T): T = {
    val headOpt = this.selectHeadOption(sql, params:_*)(block)
    if( headOpt == None ) default else headOpt.get
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
  @throws( classOf[NoSuchElementException] )
  def selectHead[T](sql: String, params: ISQLFormattable*)(block: ResultSetRow => T): T = {
    val headOpt = this.selectHeadOption(sql, params:_*)(block)
    if( headOpt == None )
      throw new NoSuchElementException("can't find a record for this SQL query: '"+sql+"'")
    else
      return headOpt.get
  }

  /**
   * Convenience method for interpreting the first column of the first record as a Long
   *
   * @param sql is a query that must return at least one record
   * @param params are the optional parameters of the query
   * @throws RuntimeException if the value is null
   * @throws SQLException if the value in the first column could not be interpreted as a Long
   * @throws NoSuchElementException if the query did not return any records.
   */
  def selectLong(sql: String, params: ISQLFormattable*): Long = {
    selectHead(sql, params.toArray: _*)(row2Long)
  }
  
  /**
   * Convenience method for interpreting the first column of the all records as a Long.
   *
   * @param sql query that should return records
   * @param params are the optional parameters used in the query
   * @return an array of loaded Long values
   * 
   */
  def selectLongs(sql: String, params: ISQLFormattable*): Array[Long] = {
    val results = new ArrayBuffer[Long]
    _selectIntoBuffer(Some(results), sql, params.toArray )(row2Long)
    results.toArray
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
    selectHead(sql, params.toArray: _*)(row2Int)
  }

  /**
   * Convenience method for interpreting the first column of the all records as a Int.
   *
   * @param sql query that should return records
   * @param params are the optional parameters used in the query
   * @return an array of loaded Int values
   * 
   */
  def selectInts(sql: String, params: ISQLFormattable*): Array[Int] = {
    val results = new ArrayBuffer[Int]
    _selectIntoBuffer(Some(results), sql, params.toArray )(row2Int)
    results.toArray
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
    selectHead(sql, params.toArray: _*)(row2Boolean)
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
    selectHead(sql, params.toArray: _*)(row2String)
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
    selectHead(sql, params.toArray: _*)(row2Float)
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
    selectHead(sql, params.toArray: _*)(row2Double)
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
    selectHead(sql, params.toArray: _*)(row2DateTime)
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
    selectHead(sql, params.toArray: _*)(row2Timestamp)
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
    selectHead(sql, params.toArray: _*)(row2Duration)
  }

  /**
   * Executes the given query and returns the number of affected records
   *
   * @param sql query that must not return any records
   * @param params are the optional parameters used in the query
   * @return the number of affected records
   */
  def execute(sql: String, params: ISQLFormattable*): Int = {
    this._usingStatement { statement =>
      val sqlString = dialect.formatSeq(sql, params.toArray)
      statement.executeUpdate( sqlString )
    }
  }

  def prepareStatementWrapper( sql: String,
                               generateKeys: Boolean = false ): PreparedStatementWrapper = {
    
    val keysOption = if (generateKeys) RETURN_GENERATED_KEYS else NO_GENERATED_KEYS
    new PreparedStatementWrapper( connection.prepareStatement(sql, keysOption), dialect )
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
    this._usingPreparedStatement(sql, generateKeys)(block)
  }
  
  def executeInBatch[T](sql: String)(block: (BatchStatementWrapper) => T): Array[Int] = {
    this._usingBatch(sql)(block)    
  }

  private def _selectIntoBuffer[T](
                buffer: Option[ArrayBuffer[T]],
                sql: String, params: Array[ISQLFormattable],
                maxRecords: Int = 0 ) (block: (ResultSetRow) => T): Unit = {
    
    this._usingStatement { statement =>
      val rs = statement.executeQuery(dialect.formatSeq(sql, params))
      val append = buffer.isDefined
      
      while (rs.next) {
        val value = block(ResultSetRow(rs))
        if (append) buffer.get.append(value)
      }
    }
  }
  
  /**
   * Creates a new statement executes the given block with it.
   * The statement is automatically closed once the block has finished.
   */
  private def _usingStatement[T](block: (Statement) => T): T = {
    
    val statement = connection.createStatement

    try {
      block(statement)
    } finally {
      // This also closes the resultset
      statement.close()
    }
  }

  /**
   * Prepares the sql query and executes the given block with it.
   * The statement is automatically closed once the block has finished.
   */
  private def _usingPreparedStatement[T]( sql: String,
                                          generateKeys: Boolean = false ) (block: (PreparedStatementWrapper) => T): T = {
    
    val statement = this.prepareStatementWrapper(sql,generateKeys)
    
    try {
      block(statement)
    } finally {
      statement.close()
    }
    
  }
  

  
  /**
   * Prepares the sql query and executes the given block in batch.
   * The statement is automatically closed once the block has finished.
   */
  private def _usingBatch[T]( sql: String )(block: (BatchStatementWrapper) => T): Array[Int] = {
    
    val statement = new BatchStatementWrapper( connection.prepareStatement(sql), dialect )
    
    try {
      block(statement)
      statement.jdbcPrepStmt.executeBatch()
    } finally {
      statement.close()
    }
    
  }
  
}
