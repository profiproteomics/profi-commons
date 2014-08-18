package fr.profi.util

import org.junit.Assert._
import org.junit.Test

import fr.profi.util.misc._

@Test
class MiscTest {
  
  @Test
  def testIfNotNull() {
    val res1 = IfNotNull( Array(1) ) { true }
    assertEquals("non null object checked",true, res1.get)
    
    val res2 = IfNotNull( null ) { true }
    assertEquals("null object checked",res2, None)
  }
  
  @Test
  def testMapIfNotNull() {
    val res1 = MapIfNotNull( Array(1) ) { data => data(0) }
    assertEquals("non null object mapped",1, res1.get)
    
    val res2 = MapIfNotNull( null ) { data => null }
    assertEquals("null object mapped",res2, None)
  }
  
  @Test
  def testInMemoryIdGen() {
    
    object ObjWithId extends InMemoryIdGen
    
    val l = (1 until 1000)
    l.par.foreach( i => ObjWithId.generateNewId() )
    
    assertEquals("obtained id after multiple and paralellel generations", -1000L, ObjWithId.generateNewId() )
  }

}