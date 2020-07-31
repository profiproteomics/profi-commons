package fr.profi.util

import org.junit.Assert._
import org.junit.Test
import java.util.Arrays
import fr.profi.util.bytes._

@Test
class BytesTest {
  
  private val base64enc = java.util.Base64.getEncoder
  
  @Test
  def testFloatsToBytes() {
    val littleEndianBytes = floatsToBytes( Array(1f,2f,3f), littleEndian = true )
    assertEquals("Array of floats to bytes", "AACAPwAAAEAAAEBA", base64enc.encodeToString(littleEndianBytes) )
    
    val bigEndianBytes = floatsToBytes( Array(1f,2f,3f), littleEndian = false )
    assertEquals("Array of floats to bytes", "P4AAAEAAAABAQAAA", base64enc.encodeToString(bigEndianBytes) )
  }

  @Test
  def testBytesToFloats() {
    val floats= Array(1f,2f,3f)
    
    val littleEndianBytes = floatsToBytes( floats , littleEndian = true )
    assertTrue(Arrays.equals(bytesTofloats(littleEndianBytes, littleEndian = true), floats))
    
    
    val bigEndianBytes = floatsToBytes( floats, littleEndian = false )
    assertTrue(Arrays.equals(bytesTofloats(bigEndianBytes, littleEndian = false), floats))
  }

  @Test
  def testDoublesToBytes() {
    val littleEndianBytes = doublesToBytes( Array(1.0,2.0,3.0), littleEndian = true )
    assertEquals("Array of doubles to bytes", "AAAAAAAA8D8AAAAAAAAAQAAAAAAAAAhA", base64enc.encodeToString(littleEndianBytes) )
    
    val bigEndianBytes = doublesToBytes( Array(1.0,2.0,3.0), littleEndian = false )
    assertEquals("Array of doubles to bytes", "P/AAAAAAAABAAAAAAAAAAEAIAAAAAAAA", base64enc.encodeToString(bigEndianBytes) )
  }
  
  
  @Test
  def testBytesToDoubles() {
    val doubles =  Array(1.0,2.0,3.0)

    val littleEndianBytes = doublesToBytes(doubles, littleEndian = true )
    assertTrue(Arrays.equals(bytesTodoubles(littleEndianBytes, littleEndian = true), doubles))
    
    
    val bigEndianBytes = doublesToBytes(doubles, littleEndian = false )
    assertTrue(Arrays.equals(bytesTodoubles(bigEndianBytes, littleEndian = false), doubles))
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
    assertEquals("Bytes to hex string", "U2ltcGxlU3RyaW5n", base64enc.encodeToString(bytes) )
  }

}