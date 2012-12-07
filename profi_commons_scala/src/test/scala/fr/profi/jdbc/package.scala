package fr.profi

package object jdbc {
  
  import java.sql.Connection
  import fr.profi.jdbc.easy.EasyDBC
  import java.sql.DriverManager  

  lazy val eDbcH2TestInstance = {
    Class.forName("org.h2.Driver")
    EasyDBC( DriverManager.getConnection("jdbc:h2:mem:edbc_test") )
  }
  
  lazy val eDbcHSQLDBTestInstance = {
    Class.forName("org.hsqldb.jdbc.JDBCDriver")
    EasyDBC( DriverManager.getConnection("jdbc:hsqldb:mem:mymemdb") )
  }
  
  lazy val eDbcSQLiteTestInstance = {
    Class.forName("org.sqlite.JDBC")
    EasyDBC(
      DriverManager.getConnection("jdbc:sqlite::memory:"),
      SQLiteSQLDialect,
      TxIsolationLevels.SERIALIZABLE
    )
  }
  
  /*object SQLiteSQLDialect2 extends AbstractSQLDialect(
    booleanFormatter = AsShortStringBooleanFormatter,
    typeMapper = SQLiteTypeMapper,
    generateKeyParam = "last_insert_rowid()"
  )
  
  lazy val eDbcSQLiteTestInstance2 = {
    Class.forName("org.sqlite.JDBC")
    EasyDBC(
      DriverManager.getConnection("jdbc:sqlite::memory:"),
      SQLiteSQLDialect2,
      TxIsolationLevels.SERIALIZABLE
    )
  }*/
  
}
