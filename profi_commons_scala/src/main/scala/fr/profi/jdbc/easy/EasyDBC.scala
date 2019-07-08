package fr.profi.jdbc.easy

import java.sql.Connection

import fr.profi.jdbc.AbstractSQLDialect
import fr.profi.jdbc.DefaultSQLDialect
import fr.profi.jdbc.SQLQueryExecution
import fr.profi.jdbc.TransactionManagement
import fr.profi.jdbc.TxIsolationLevels

class EasyDBC (
  val connection: Connection,
  val dialect: AbstractSQLDialect = DefaultSQLDialect,
  val txIsolationLevel: TxIsolationLevels.Value = TxIsolationLevels.READ_COMMITTED
) extends TransactionManagement with SQLQueryExecution {
  
  def inTx[T]( txCode: (EasyDBC) => T ): T = {
    
    this.beginTransaction()
    
    try {
      val result = txCode( this )
      if( this.isInTransaction ) this.commitTransaction()
      result
    } catch {
      case e: Throwable => {
        this.rollbackTransaction()
        throw e
      }
    }
    
  }
  
}
 
object EasyDBC {
  def apply(c: Connection,
            f: AbstractSQLDialect = DefaultSQLDialect,
            i: TxIsolationLevels.Value = TxIsolationLevels.READ_COMMITTED
            ) = new EasyDBC(c, f, i)
}

class SimpleQueryMaker(val connection: Connection, val dialect: AbstractSQLDialect) extends SQLQueryExecution

object SimpleQueryMaker {
  def apply(c: Connection, f: AbstractSQLDialect) = new SimpleQueryMaker(c, f)
}
