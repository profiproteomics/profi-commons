package fr.profi.util

import java.io.File
import java.io.InputStream
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.xml.factory.XMLLoader
import fr.profi.util.primitives._
import fr.profi.util.resources.pathToFileOrResourceToFile

/**
 * @author David Bouyssie
 *
 */
package object dbunit {
  
  private def newXmlLoader(): XMLLoader[xml.Elem] = {
    
    // Workaround for issue "Non-namespace-aware mode not implemented"
    // We use the javax SAXParserFactory with a custom configuration
    // Source:  http://stackoverflow.com/questions/11315439/ignore-dtd-specification-in-scala
    val saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance()
    // TODO: check if it also works without settting validating to false
    saxParserFactory.setValidating(false)

    // Instantiate the XML loader using the javax SAXParser
    xml.XML.withSAXParser(saxParserFactory.newSAXParser)
  }
  
  def parseDbUnitDataset( datasetLocation: File, lowerCase: Boolean ): Array[(String,ArrayBuffer[StringMap])] = {
    parseDbUnitDataset( datasetLocation, newXmlLoader(), lowerCase )
  }
  
  def parseDbUnitDataset( datasetLocation: File, xmlLoader: xml.factory.XMLLoader[xml.Elem], lowerCase: Boolean ): Array[(String,ArrayBuffer[StringMap])] = {

    // Load the dataset
    val xmlDoc = xmlLoader.loadFile( datasetLocation )
    
    parseDbUnitDataset( xmlDoc, lowerCase )
  }
  
  def parseDbUnitDataset( datasetStream: InputStream, lowerCase: Boolean ): Array[(String,ArrayBuffer[StringMap])] = {
    parseDbUnitDataset( datasetStream, newXmlLoader(), lowerCase )
  }
  
  def parseDbUnitDataset( datasetStream: InputStream, xmlLoader: xml.factory.XMLLoader[xml.Elem], lowerCase: Boolean ): Array[(String,ArrayBuffer[StringMap])] = {

    // Load the dataset
    val xmlDoc = xmlLoader.load( datasetStream )
    
    parseDbUnitDataset( xmlDoc, lowerCase )
  }
  
  def parseDbUnitDataset( datasetAsXML: xml.Elem, lowerCase: Boolean ): Array[(String,ArrayBuffer[StringMap])] = {
    
    val recordsByTableName = new HashMap[String,ArrayBuffer[StringMap]]
    val tableNames = new ArrayBuffer[String]()
    
    // Iterate over dataset nodes
    for( xmlNode <- datasetAsXML.child ) {
      
      val attrs = xmlNode.attributes
      
      // Check node has defined attributes
      if( attrs.isEmpty == false ) {
        
        val tableName = if( lowerCase ) xmlNode.label.toLowerCase()
        else xmlNode.label.toUpperCase()
        
        if( recordsByTableName.contains(tableName) == false )
          tableNames += tableName
        
        val record = new StringMap()
        
        // Iterate over node attributes
        for( attr <- attrs ) {
          val attrKey = if( lowerCase ) attr.key.toLowerCase()
          else attr.key.toUpperCase()
          
          record += attrKey -> attr.value.text
        }
        
        // Append record to the records of this table
        val records = recordsByTableName.getOrElseUpdate(tableName, new ArrayBuffer[StringMap]() )
        records += record
      }
    }
    
    tableNames.map( tblName => (tblName -> recordsByTableName(tblName)) ).toArray
  }

}