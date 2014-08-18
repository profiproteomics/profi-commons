package fr.profi.util

package object random {
  
  import scala.util.Random

  def randomString(chars: String, lengthMin: Int, lengthMax: Int): String = {
    require(chars != null,"chars is null")
    require(chars.isEmpty == false,"chars is empty")
    require(lengthMin > 0,"lengthMin must be strictly positive")
    
    val length = randomInt(lengthMin, lengthMax)
    
    val newKey = (1 to length).map( x => {
      val index = Random.nextInt(chars.length)
      chars(index)
    }).mkString("")
    
    newKey
  }

  def randomInt(minInclu: Int, maxInclu: Int): Int = {
    require(minInclu <= maxInclu)
    
    if (minInclu == maxInclu) minInclu
    else Random.nextInt(maxInclu + 1 - minInclu) + minInclu
  }

  def randomFloat(minInclu: Float, maxExclu: Float): Float = {
    require(minInclu <= maxExclu)
    
    if (minInclu == maxExclu)
      minInclu
    else
      Random.nextFloat() * (maxExclu - minInclu) + minInclu
  }
  
  def randomDouble(minInclu: Double, maxExclu: Double): Double = {
    require(minInclu <= maxExclu)
    
    if (minInclu == maxExclu)
      minInclu
    else
      Random.nextDouble() * (maxExclu - minInclu) + minInclu
  }
  
  def randomGaussian(minInclu: Double, maxInclu: Double, stddev: Double): Double = {
    require(minInclu <= maxInclu)
    
    if (minInclu == maxInclu)
      minInclu
    else {
      val randomValue = Random.nextGaussian * stddev + (minInclu + maxInclu)/2
      
      if( randomValue < minInclu ) minInclu
      else if ( randomValue > maxInclu ) maxInclu
      else randomValue
    }
  }
  
  def randomGaussian( mean: Double, stdDev: Double ): Double = {
    (Random.nextGaussian() * stdDev) + mean
  }
  
}