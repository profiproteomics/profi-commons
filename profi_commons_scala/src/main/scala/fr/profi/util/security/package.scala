package fr.profi.util

import java.security.MessageDigest
import fr.proline.util.bytes._

package object security {
  
  def md5Hex( text: String ): String = {
    val md = MessageDigest.getInstance("MD5")
    md.update(text.getBytes("UTF-8"))
    bytes2HexString(md.digest())
  }
  
  def sha256Hex( text: String ): String = {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(text.getBytes("UTF-8"))
    bytes2HexString(md.digest())
  }

}