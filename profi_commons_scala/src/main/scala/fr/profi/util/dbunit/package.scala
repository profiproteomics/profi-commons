package fr.profi.util

import java.io.File
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import fr.profi.util.primitives._
import fr.profi.util.resources.pathToFileOrResourceToFile

/**
 * @author David Bouyssie
 *
 */
package object dbunit {
  
  def parseDbUnitDataset( datasetLocation: File, lowerCase: Boolean ): Map[String,ArrayBuffer[StringMap]] = {
    
    // Workaround for issue "Non-namespace-aware mode not implemented"
    // We use the javax SAXParserFactory with a custom configuration
    // Source:  http://stackoverflow.com/questions/11315439/ignore-dtd-specification-in-scala
    val saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance()
    // TODO: check if it also works without settting validating to false
    saxParserFactory.setValidating(false)

    // Instantiate the XML loader using the javax SAXParser
    val xmlLoader = xml.XML.withSAXParser(saxParserFactory.newSAXParser)
    
    parseDbUnitDataset( datasetLocation, xmlLoader, lowerCase )
  }
  
  def parseDbUnitDataset( datasetLocation: File, xmlLoader: xml.factory.XMLLoader[xml.Elem], lowerCase: Boolean ): Map[String,ArrayBuffer[StringMap]] = {

    // Load the dataset
    val xmlDoc = xmlLoader.loadFile( datasetLocation )
    
    parseDbUnitDataset( xmlDoc, lowerCase )
  }
  
  def parseDbUnitDataset( datasetAsXML: xml.Elem, lowerCase: Boolean ): Map[String,ArrayBuffer[StringMap]] = {
    
    val recordsByTableName = new HashMap[String,ArrayBuffer[StringMap]]
    
    // Iterate over dataset nodes
    for( xmlNode <- datasetAsXML.child ) {
      
      val attrs = xmlNode.attributes
      
      // Check node has defined attributes
      if( attrs.isEmpty == false ) {
        
        val tableName = if( lowerCase ) xmlNode.label.toLowerCase()
        else xmlNode.label.toUpperCase()
        
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
    
    recordsByTableName.toMap
  }

}