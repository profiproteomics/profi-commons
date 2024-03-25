package fr.profi.util

import org.junit.Assert._
import org.junit.Test

import fr.profi.util.regex.RegexUtils._

@Test
class RegexTest {
  
  @Test
  def testRichString() {
    assertTrue("regular expression provided as a String", "hello world" =~ """\w+\s\w+""" )
    assertTrue("regular expression provided as a scala Regex object", "hello world" =~ """\w+\s\w+""".r )
    assertTrue("partial matching check", "hello world and other words" ~~ """\w+\s\w+""" )
    assertFalse("no match check", "hello" =~ """\w+\s\w+""" )
    assertFalse("no partial match check", "hello" ~~ """\w+\s\w+""" )
    
    assertEquals( "group capture success", "hello", ("hello world" =# """\w+""").get.group(0) )
    assertEquals( "group capture failure", None, "hello world" =# """\d+""" )
    assertEquals( "group capture 2", 2.01, ("pif:2.01" =# """pif:(\d+\.\d+)""").get.group(1).toFloat, 0.001 )
  }
  
  @Test
  def testRichRegex() {
    assertTrue("regex versus string matching check", """\w+\s\w+""".r ~= "hello world"  )
    assertFalse("regex versus string no match check", """\w+\s\w+""".r ~= "hello" )
  }

}