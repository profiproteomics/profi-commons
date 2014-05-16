package fr.profi.util

import org.junit.Assert._
import org.junit.Test
import java.io.File
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import scala.collection.mutable.ArrayBuffer
import scala.io.Source
import fr.proline.util.io._


@Test
class IOTest {
  
  private val twoLines = Array("Line1", "Line2")
  private val LINE_SEP = System.getProperty("line.separator")
  
  @Test
  def testLineIterator() {
    
    // Create and open TEMP file
    val tempFile = _createTempFile()
    val source = Source.fromFile(tempFile)
    
    // Iterate over each line of the file
    val lineIter = new LineIterator(source, LINE_SEP)
    val lineBuffer = new ArrayBuffer[String]
    while( lineIter.hasNext ) {
      val line = lineIter.next()
      lineBuffer += line
    }
    
    // Close and delete TEMP file
    source.close()
    tempFile.delete()
    
    assertTrue( "file content should equals the twoLines array", twoLines.sameElements(lineBuffer) )
  }
  
  @Test
  def testRichBufferedSource() {
    
    // Create and open TEMP file
    val tempFile = _createTempFile()
    val source = Source.fromFile(tempFile)
    
    // Iterate over each line of the file
    val lineBuffer = new ArrayBuffer[String]
    source.eachLine { line => lineBuffer += line }
    
    // Close and delete TEMP file
    source.close()
    tempFile.delete()
    
    assertTrue( "file content should equals the twoLines array", twoLines.sameElements(lineBuffer) )
  }
  
  private def _createTempFile(): File = {
    val tempFile = Files.createTempFile("tempfiles", ".tmp")
    val content = twoLines.mkString(LINE_SEP)
    
    Files.write(
      tempFile,
      content.getBytes("utf-8"),
      StandardOpenOption.WRITE
    )
    
    tempFile.toFile
  }

}