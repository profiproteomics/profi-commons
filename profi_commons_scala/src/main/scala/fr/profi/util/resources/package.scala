package fr.profi.util

import java.io.{InputStream,File,FileInputStream}

/**
 * @author David Bouyssie
 *
 */

package object resources {
  
  def pathToFileOrResourceToFile( path: String, resClass: Class[_] ): File = {
    val file = new File(path)
    
    if( file.isFile == true) return file
    else {
      val fileURL = resClass.getResource(path)
      if( fileURL != null ) {
        val f = new File(fileURL.toURI())
  
        if (f.isFile) return f
        else return null
      }
    }
    
    null
  }
  
  def pathToStreamOrResourceToStream( path: String, resClass: Class[_] ): InputStream = {
    val file = new File(path)
    
    if (file.isFile == true) new FileInputStream(file.getAbsolutePath)
    else resClass.getResourceAsStream(path)
  }
  
}