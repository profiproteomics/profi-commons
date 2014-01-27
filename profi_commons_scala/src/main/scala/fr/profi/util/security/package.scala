package fr.profi.util

import java.security.MessageDigest
import fr.proline.util.bytes._

package object security {
  
  def md5Hex( password: String ): String = {
    val md = MessageDigest.getInstance("MD5")
    md.update(password.getBytes())
    bytes2HexString(md.digest())
  }

}