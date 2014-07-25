package fr.profi.util

import org.junit.Test
import org.junit.Assert

class SQLTest extends {

  @Test
  def testPgCopyEscape() {
    val str = "anti-slash\\ CR\r LN \n  Tab	"
    val expected = """anti-slash\\ CR LN \n  Tab\t"""

    val escaped = fr.profi.util.sql.escapeStringForPgCopy(str)

    Assert.assertEquals("PgCopy String escape", expected, escaped)
  }

}
