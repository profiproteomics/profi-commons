package fr.profi.util

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import fr.profi.util.resources.pathToFileOrResourceToFile

/**
 * @author David Bouyssie
 *
 */
package object dbunit {
  
  def parseDbUnitDataset( datasetPath: String ): Map[String,ArrayBuffer[Map[String,String]]] = {
    
    // Workaround for issue "Non-namespace-aware mode not implemented"
    // We use the javax SAXParserFactory with a custom configuration
    // Source:  http://stackoverflow.com/questions/11315439/ignore-dtd-specification-in-scala
    val saxParserFactory = javax.xml.parsers.SAXParserFactory.newInstance()
    // TODO: check if it also works without settting validating to false
    saxParserFactory.setValidating(false)

    // Instantiate the XML loader using the javax SAXParser
    val xmlLoader = xml.XML.withSAXParser(saxParserFactory.newSAXParser)
    
    parseDbUnitDataset( datasetPath, xmlLoader )
  }
  
  def parseDbUnitDataset( datasetPath: String, xmlLoader: xml.factory.XMLLoader[xml.Elem] ): Map[String,ArrayBuffer[Map[String,String]]] = {

    // Load the dataset
    val xmlDoc = xmlLoader.loadFile( pathToFileOrResourceToFile(datasetPath,this.getClass) )
    
    parseDbUnitDataset( xmlDoc )
  }
  
  def parseDbUnitDataset( datasetAsXML: xml.Elem ): Map[String,ArrayBuffer[Map[String,String]]] = {
    
    val recordsByTableName = new HashMap[String,ArrayBuffer[Map[String,String]]]
    
    // Iterate over dataset nodes
    for( xmlNode <- datasetAsXML.child ) {
      
      val attrs = xmlNode.attributes
      
      // Check node has defined attributes
      if( attrs.isEmpty == false ) {
        
        val tableName = xmlNode.label
        
        val recordBuilder = Map.newBuilder[String,String]
        
        // Iterate over node attributes
        for( attr <- attrs ) {
          val str = attr.value.text
          recordBuilder += attr.key -> attr.value.text
        }
        
        // Append record to the records of this table
        val records = recordsByTableName.getOrElseUpdate(tableName, new ArrayBuffer[Map[String,String]]() )
        records += recordBuilder.result()
        
      }
    }
    
    recordsByTableName.toMap
  }

}