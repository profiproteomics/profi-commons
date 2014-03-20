package fr.proline.util

package object bytes {
  
  import java.nio.{ByteBuffer,ByteOrder,DoubleBuffer}
  
  def floatsToBytes( floats: Array[Float], littleEndian: Boolean = true ): Array[Byte] = {
    
    val endianess = if(littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN
    
    // Convert float to a byte buffer
    val byteBuf = ByteBuffer.allocate(4 * floats.length).order(endianess)
    floats.foreach { byteBuf.putFloat(_) }

    // Convert byte buffer into a byte array
    byteBuf.array()
  }
  
  def doublesToBytes( doubles: Array[Double], littleEndian: Boolean = true ): Array[Byte] = {
    
    val endianess = if(littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN
    
    // Convert doubles to a byte buffer
    val byteBuf = ByteBuffer.allocate(8 * doubles.length).order(endianess)
    doubles.foreach { byteBuf.putDouble(_) }

    // Convert byte buffer into a byte array
    byteBuf.array()
  }
  
  private val HEX_CHARS = "0123456789abcdef".toCharArray()

  def bytes2HexString( bytes: Array[Byte] ): String = {
    new String( bytes2HexChars(bytes) )
  }
  
  def bytes2HexChars( bytes: Array[Byte] ): Array[Char] = {
    val chars = new Array[Char]( 2 * bytes.length )
    
    var i = 0
    while (i < bytes.length) {
      val b = bytes( i )
      chars( 2 * i ) = HEX_CHARS( ( b & 0xF0 ) >>> 4 )
      chars( 2 * i + 1 ) = HEX_CHARS( b & 0x0F )
      i = i + 1
    }
    
    chars
  }
  
  def hexString2Bytes(str: String): Array[Byte] = {
    val bytes = new Array[Byte](str.length / 2)
    
    var i = 0
    while (i < bytes.length) {
      bytes(i) = Integer.parseInt(str.substring(2 * i, 2 * i + 2), 16).toByte
      i += 1
    }
    
    bytes
  }
  
}