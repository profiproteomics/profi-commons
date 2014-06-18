package fr.profi.util

import org.junit.Assert._
import org.junit.Test

import fr.profi.util.bytes._

@Test
class BytesTest {
  
  private val base64enc = new sun.misc.BASE64Encoder()
  
  @Test
  def testFloatsToBytes() {
    val littleEndianBytes = floatsToBytes( Array(1f,2f,3f), littleEndian = true )
    assertEquals("Array of floats to bytes", "AACAPwAAAEAAAEBA", base64enc.encode(littleEndianBytes) )
    
    val bigEndianBytes = floatsToBytes( Array(1f,2f,3f), littleEndian = false )
    assertEquals("Array of floats to bytes", "P4AAAEAAAABAQAAA", base64enc.encode(bigEndianBytes) )
  }
  
  @Test
  def testDoublesToBytes() {
    val littleEndianBytes = doublesToBytes( Array(1.0,2.0,3.0), littleEndian = true )
    assertEquals("Array of doubles to bytes", "AAAAAAAA8D8AAAAAAAAAQAAAAAAAAAhA", base64enc.encode(littleEndianBytes) )
    
    val bigEndianBytes = doublesToBytes( Array(1.0,2.0,3.0), littleEndian = false )
    assertEquals("Array of doubles to bytes", "P/AAAAAAAABAAAAAAAAAAEAIAAAAAAAA", base64enc.encode(bigEndianBytes) )    
  }
  
  @Test
  def testBytes2HexString() {
    val hexStr = bytes2HexString( "SimpleString".getBytes )
    assertEquals("Bytes to hex string", "53696d706c65537472696e67", hexStr )
  }
  
  @Test
  def testBytes2HexChars() {
    val hexChars = bytes2HexChars( "SimpleString".getBytes )
    assertEquals("Bytes to hex string", "53696d706c65537472696e67", new String(hexChars) )
  }
  
  @Test
  def testHexString2Bytes() {
    val bytes = hexString2Bytes( "53696d706c65537472696e67" )
    assertEquals("Bytes to hex string", "U2ltcGxlU3RyaW5n", base64enc.encode(bytes) )
  }

}