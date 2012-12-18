package fr.proline.util

package object primitives {

  object LongOrIntAsInt {
    
    def asInt(num: AnyVal): Int = num match {
      case i: Int => i
      case l: Long => l.toInt
      case _ => throw new IllegalArgumentException("can't only take a Int or a Long as input")
    }
    implicit def anyVal2Int( num: AnyVal ): Int = asInt( num )
    
  }
  
  object DoubleOrFloatAsFloat {
    
    def asFloat(num: AnyVal): Float = num match {
      case f: Float => f
      case d: Double => d.toFloat
      case _ => throw new IllegalArgumentException("can't only take a Double or a Float as input")
    }
    implicit def anyVal2Float( num: AnyVal ): Float = asFloat( num )
    
  }
}