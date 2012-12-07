package fr.profi.jdbc

import java.sql.Connection
import java.sql.Statement

import java.sql.Statement.RETURN_GENERATED_KEYS
import java.sql.Statement.NO_GENERATED_KEYS

/**
 * Private class providing methods for using Statements and
 * ReusableStatements.
 */
private[jdbc] class RichConnection(val jdbcConnection: Connection) {
  
  /**
   * Creates a new statement executes the given block with it.
   * The statement is automatically closed once the block has finished.
   */
  def usingStatement[T](block: (Statement) => T): T = {
    
    val statement = jdbcConnection.createStatement

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
  def usingPreparedStatement[T]( sql: String,
                                 dialect: AbstractSQLDialect,
                                 generateKeys: Boolean = false ) (block: (PreparedStatementWrapper) => T): T = {
    
    val keysOption = if (generateKeys) RETURN_GENERATED_KEYS else NO_GENERATED_KEYS
    val statement = new PreparedStatementWrapper( jdbcConnection.prepareStatement(sql, keysOption), dialect )
    
    try {
      block(statement)
    } finally {
      statement.close()
    }
    
  }
  
  /**
   * Prepares the sql query and executes the given block with it.
   * The statement is automatically closed once the block has finished.
   */
  def usingBatch[T]( sql: String,
                     dialect: AbstractSQLDialect )(block: (BatchStatementWrapper) => T): Array[Int] = {
    
    val statement = new BatchStatementWrapper( jdbcConnection.prepareStatement(sql), dialect )
    
    try {
      block(statement)
      statement.jdbcPrepStmt.executeBatch()
    } finally {
      statement.close()
    }
    
  }
  
}

private[jdbc] object RichConnection {

  implicit def conn2RichConn(conn: Connection): RichConnection = {
    new RichConnection(conn)
  }
}
