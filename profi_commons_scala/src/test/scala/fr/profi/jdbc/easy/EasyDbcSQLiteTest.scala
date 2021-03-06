package fr.profi.jdbc.easy

import org.junit.Test
import org.junit.Assert.assertEquals

class EasyDbcSQLiteTest {

  @Test
  def simpleTest() {
    
    val ezDBC = fr.profi.jdbc.eDbcSQLiteTestInstance
    ezDBC.execute("CREATE TABLE person (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, age INTEGER)")
    
    ezDBC.beginTransaction()
    
    var generatedIntSum: Int = 0
    var generatedLongSum: Long = 0L
    ezDBC.executePrepared("INSERT INTO person VALUES (NULL, ?, ?)",true) { stmt =>
      for ( i <- 1 to 3 ) {   
        stmt.executeWith( new java.util.Date, 1 )
        generatedIntSum += stmt.generatedInt
        generatedLongSum += stmt.generatedLong
      }
    }
    
    ezDBC.commitTransaction()
    
    assertEquals( 3, ezDBC.selectInt("SELECT count(*) FROM person") )
    assertEquals( 6, generatedIntSum )
    assertEquals( 6L, generatedLongSum )
    
    println( ezDBC.select("SELECT name FROM person") { r => r.nextStringOrElse("") } (0) )
    
  }
  

}
