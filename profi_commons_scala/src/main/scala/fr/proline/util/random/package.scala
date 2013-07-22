package fr.proline.util

import scala.util.Random

object random {

  def randomString(chars: String, lengthMin: Int, lengthMax: Int): String = {
    
    val length = randomInt(lengthMin, lengthMax)
    
    val newKey = (1 to length).map( x => {
        val index = Random.nextInt(chars.length)
        chars(index)
      }
    ).mkString("")
    
    newKey
  }

  def randomInt(minInclu: Int, maxInclu: Int): Int = {
    require(minInclu <= maxInclu)
    
    if (minInclu == maxInclu)
      minInclu
    else
      Random.nextInt(maxInclu + 1 - minInclu) + minInclu
  }

  def randomDouble(minInclu: Double, maxExclu: Double): Double = {
    require(minInclu <= maxExclu)
    
    if (minInclu == maxExclu)
      minInclu
    else
      Random.nextDouble() * (maxExclu - minInclu) + minInclu
  }
  
}