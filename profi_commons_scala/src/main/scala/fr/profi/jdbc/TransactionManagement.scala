package fr.profi.jdbc

import com.typesafe.scalalogging.LazyLogging

object TxIsolationLevels extends Enumeration {
  val NONE = Value(java.sql.Connection.TRANSACTION_NONE)
  val READ_COMMITTED = Value(java.sql.Connection.TRANSACTION_READ_COMMITTED)
  val READ_UNCOMMITTED = Value(java.sql.Connection.TRANSACTION_READ_UNCOMMITTED)
  val REPEATABLE_READ = Value(java.sql.Connection.TRANSACTION_REPEATABLE_READ)
  val SERIALIZABLE = Value(java.sql.Connection.TRANSACTION_SERIALIZABLE)
}

trait TransactionManagement extends LazyLogging {

  protected val connection: java.sql.Connection
  protected val txIsolationLevel: TxIsolationLevels.Value
  //protected var inTransaction: Boolean = false

  /**
   * Begins the Transaction.
   *
   * @throws SQLException if transaction could not be rollbacked
   */
  def beginTransaction(): Unit = {

    // Change the connection config to be ready for a new transaction
    this.connection.setAutoCommit(false)

    val oldTransactionIsolation = connection.getTransactionIsolation
    val newTransactionIsolation = txIsolationLevel.id

    if (oldTransactionIsolation != newTransactionIsolation) {
      logger.warn("Current TransactionIsolation level: " + oldTransactionIsolation + "  Unable to change to: " + newTransactionIsolation)
      // txIsolationLevel is handled by the DatabaseConnectionContext
      // TODO: find a way ton handle this here when a DatabaseConnectionContext is not used
      // this.connection.setTransactionIsolation( this.txIsolationLevel.id )
    }

    ()
  }

  /**
   * Rollbacks the Transaction.
   *
   * @throws SQLException if transaction could not be rollbacked
   */
  @throws(classOf[java.sql.SQLException])
  def rollbackTransaction(): Unit = {
    this.connection.rollback()
    //this.inTransaction = false
    this.connection.setAutoCommit(true)
  }
  def rollback() = rollbackTransaction()

  /**
   * Commits all changed done in the Transaction.
   *
   * @throws SQLException if transaction could not be committed.
   */
  @throws(classOf[java.sql.SQLException])
  def commitTransaction(): Unit = {
    this.connection.commit()
    this.connection.setAutoCommit(true)
  }
  def commit() = commitTransaction()

  def isInTransaction(): Boolean = !this.connection.getAutoCommit

}
