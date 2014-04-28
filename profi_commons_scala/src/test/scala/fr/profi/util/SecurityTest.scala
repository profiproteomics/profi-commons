package fr.profi.util

import org.junit.Assert._
import org.junit.Test
import fr.profi.util.security._


@Test
class SecurityTest {


  @Test
  def testHashString() {
    assertEquals("MD5 Password ", md5Hex("MyPasswordTest"), "514f99f887711c980f2ff6bef1c477d0" )
    assertEquals("SHA256 Password ", sha256Hex("MyPasswordTest"), "2ce97f4c26308c59467c19575595ab8728ec5bf6fd54f2ceb632dfb83ffb64a9" )
  }


}
