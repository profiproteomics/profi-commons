package fr.profi.util

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.AssertionsForJUnit

import sql._

class SQLTest extends AssertionsForJUnit {
  
  @Test
  def testBoolToSQLStr() {
    assertEquals("convert true to 't' char","t", BoolToSQLStr(true) )
    assertEquals("convert false to 'f' char","f", BoolToSQLStr(false) )
    assertEquals("convert true to '1' char","1", BoolToSQLStr(true, asInt = true ) )
    assertEquals("convert false to '0' char","0", BoolToSQLStr(false, asInt = true ) )    
  }
  
 @Test
  def testSQLStrToBool() {
    assertTrue( SQLStrToBool("true") )
    assertFalse( SQLStrToBool("false") )
    assertTrue( SQLStrToBool("t") )
    assertFalse( SQLStrToBool("f") )
    assertTrue( SQLStrToBool("1") )
    assertFalse( SQLStrToBool("0") )
    intercept[IllegalArgumentException]( SQLStrToBool(null) )
  }
 
  @Test
  def testStringOrBoolAsBool() {
    
    import StringOrBoolAsBool._
    
    val bool: Boolean = "true"
    assertTrue( bool )
    
    intercept[IllegalArgumentException] {
      val invalidBool: Boolean = "hello"
    }
  }
  
  @Test
  def testGetTimeAsSQLTimestamp() {
    val timeStamp = getTimeAsSQLTimestamp()
    assertTrue( timeStamp.getTime() >= new java.util.Date().getTime )
  }

  @Test
  def testPgCopyEscape() {
    val str = "anti-slash\\ CR\r LN \n  Tab	"
    val expected = """anti-slash\\ CR LN \n  Tab\t"""

    val escaped = escapeStringForPgCopy(str)

    assertEquals("PgCopy String escape", expected, escaped)
  }
  
  @Test
  def testEncodeRecordForPgCopy() {
    val encodedBytes = encodeRecordForPgCopy( List("black",255,255,255) )
    val recordAsHexStr = fr.profi.util.bytes.bytes2HexString(encodedBytes)
    assertEquals("PgCopy data encoding","626c61636b0932353509323535093235350a",recordAsHexStr)
  }
  
  @Test
  def testNewDecimalFormat() {
    val formatter = newDecimalFormat("#.#")
    assertEquals("decimal formatting", "1.2", formatter.format(1.2001) )
  }

}
