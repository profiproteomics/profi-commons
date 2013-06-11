package fr.profi.jdbc

import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.Timestamp
import java.sql.Types

import org.joda.time.DateTime

import fr.proline.util.primitives._

/**
 * Wrapper around PreparedStatement making is easier to add parameters.
 *
 * The ReusableStatement can be used in two ways.
 *
 * ## Add parameters and then execute as a chain
 *     statement << param1 << param2 << param3 <<!
 *
 * ## Set parameters and execute in on shot
 *     statement.executeWith( param1, param2, param3 )
 */
trait StatementWrapper {
  
  val jdbcPrepStmt: PreparedStatement
  val dialect: AbstractSQLDialect
  
  protected val _startIndex = 1
  protected var _parameterIndex = _startIndex
  
  /**
   * Executes the statement with the previously set parameters
   * @return the number of affected records
   */
  def execute(): Int
  
  /**
   * Sets all parameters and executes the statement
   * @return the number of affected records
   */
  def executeWith( params: ISQLFormattable* ): Int
  
  /**
   * Adds the param to the query and returns this so that it
   * possible to chain several calls together
   * @return self to allow for chaining calls
   */
  def <<( param: ISQLFormattable ): StatementWrapper = {
    param.addTo(this)
    this
  }

  /**
   * Alias of execute() included to look good with the <<
   * @return the number of affected records
   */
  def <<!(): Int = execute()

  /**
   * Add a String to the current parameter index
   */
  def addString( value: String ) = addValue( () =>
    jdbcPrepStmt.setString( _parameterIndex, dialect.escapeString(value) )
  )

  /**
   * Add a Date to the current parameter index. This is done by setTimestamp which
   * looses the Timezone information of the DateTime
   */
  def addDateTime( value: DateTime ): Unit = addValue( () =>
    jdbcPrepStmt.setTimestamp(_parameterIndex, new Timestamp(value.getMillis))
  )
  
  /**
   * Add a Timestamp to the current parameter index. 
   * 
   */
  def addTimestamp( value: Timestamp ): Unit = addValue( () =>
    jdbcPrepStmt.setTimestamp(_parameterIndex, value )
  )
  
  /**
   * Add a Boolean to the current parameter index
   */
  def addBoolean(value: Boolean): Unit = addValue( () => jdbcPrepStmt.setBoolean(_parameterIndex, value) )

  /**
   * Add a Long to the current parameter index
   */
  def addLong(value: Long): Unit = addValue( () => jdbcPrepStmt.setLong(_parameterIndex, value) )

  /**
   * Add a Int to the current parameter index
   */
  def addInt(value: Int): Unit = addValue( () => jdbcPrepStmt.setInt(_parameterIndex, value) )

  /**
   * Add a Float to the current parameter index
   */
  def addFloat(value: Float): Unit = addValue( () => jdbcPrepStmt.setFloat(_parameterIndex, value) )

  /**
   * Add a Double to the current parameter index
   */
  def addDouble(value: Double): Unit = addValue( () => jdbcPrepStmt.setDouble(_parameterIndex, value) )

  /**
   * Add a Bytes to the current parameter index
   */
  def addBytes(value: Array[Byte]): Unit = addValue( () => jdbcPrepStmt.setBytes(_parameterIndex, value) )

  /**
   * Add Null to the current parameter index
   */
  def addNull(): Unit = addValue( () => jdbcPrepStmt.setNull(_parameterIndex, Types.NULL) )
  
  private def addValue( bindingFunc: () => Unit) = {
    bindingFunc.apply()
    _parameterIndex = _parameterIndex + 1
  }

  def close() = jdbcPrepStmt.close()
}

class PreparedStatementWrapper(
  val jdbcPrepStmt: PreparedStatement,
  val dialect: AbstractSQLDialect ) extends StatementWrapper {
  
  /**
   * Executes the statement with the previously set parameters
   * @return the number of affected records
   */
  def execute(): Int = {
    this._parameterIndex = _startIndex
    jdbcPrepStmt.executeUpdate()
  }

  /**
   * Sets all parameters and executes the statement
   * @return the number of affected records
   */
  def executeWith( params: ISQLFormattable* ): Int = {
    params.foreach( param => param.addTo( this ) )
    execute
  }
  
  def generatedInt: Int = {
    
    val rsWithGenKeys = this.jdbcPrepStmt.getGeneratedKeys()
    
    dialect.generateKeyParam match {
      case s: String => rsWithGenKeys.getInt(s)
      case i: Int => if( rsWithGenKeys.next() ) rsWithGenKeys.getInt(i) else 0
    }
    
  }
  
  def generatedLong: Long = {
    
    val rsWithGenKeys = this.jdbcPrepStmt.getGeneratedKeys()
    
    dialect.generateKeyParam match {
      case s: String => toLong(rsWithGenKeys.getObject(s))
      case i: Int => if (rsWithGenKeys.next()) toLong(rsWithGenKeys.getObject(i)) else 0L
    }
    
  }
  
}
  
class BatchStatementWrapper(    
  override val jdbcPrepStmt: PreparedStatement,
  override val dialect: AbstractSQLDialect
  
) extends PreparedStatementWrapper(jdbcPrepStmt,dialect) {
  
  /**
   * Executes the statement with the previously set parameters
   * @return the number of affected records
   */
  override def execute(): Int = {
    this._parameterIndex = _startIndex
    jdbcPrepStmt.addBatch()
    1
  }
  
}
