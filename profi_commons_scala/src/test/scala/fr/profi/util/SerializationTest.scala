package fr.profi.util

import org.junit.Assert._
import org.junit.Test

@Test
class SerializationTest {
  
  @Test
  def testMsgPack {
    import fr.profi.util.serialization._
    
    val input = Array(Map(1 -> 2), Map("hello" ->"world"))
    val bytes = ProfiMsgPack.serialize(input)
    val output = ProfiMsgPack.deserialize[Array[Map[Any,Any]]](bytes)
    
    val inputAsJson = ProfiJson.serialize(input)
    val outputAsJson = ProfiJson.serialize(output)

    assertEquals(inputAsJson,outputAsJson)
  }
  
  
}