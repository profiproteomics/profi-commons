package fr.profi.jdbc.easy

import org.junit.Test
import org.junit.Assert.assertEquals

class EasyDbcSQLiteTest {

  @Test
  def simpleTest() {
    
    val eDBC = fr.profi.jdbc.eDbcSQLiteTestInstance
    eDBC.execute("CREATE TABLE person (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER)")
    
    eDBC.beginTransaction()
    
    var generatedIntSum: Int = 0
    eDBC.executePrepared("INSERT INTO person VALUES (NULL, ?, ?)",true) { stmt =>
      for ( i <- 1 to 3 ) {   
        stmt.executeWith( new java.util.Date, 1 )
        generatedIntSum += stmt.generatedInt()
      }
    }
    
    eDBC.commitTransaction()
    
    assertEquals( 3, eDBC.selectInt("SELECT count(*) FROM person") )
    assertEquals( 6, generatedIntSum )
    
    println( eDBC.select("SELECT name FROM person") { r => r.nextStringOrElse("") } (0) )
    
  }
  

}
