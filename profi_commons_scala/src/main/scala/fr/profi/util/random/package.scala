package fr.profi.util

package object random {
  
  import scala.collection.mutable.ArrayBuffer
  import scala.util.Random
  import org.apache.commons.math3.stat.StatUtils
  import fr.profi.util.primitives.isZeroOrNaN

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
  
  def generatePositiveGaussianValues(
    expectedMean: Double,
    expectedStdDev: Double,
    stdDevTol: Float = 0.05f,
    nbValues: Int = 3,
    maxIterations: Int = 50000
  ): Array[Double] = {
    require( expectedMean.isNaN == false, "mean is NaN")
    require( isZeroOrNaN(expectedStdDev) == false, "expectedStdDev equals zero or is NaN")
    require( stdDevTol >= 0, "stdDevTol must be greater than zero")
    require( nbValues >= 3, "can't generate less than 3 values to obtain the expectedStdDev")
    require( maxIterations >= 0, "maxIterations must be greater than zero")
    
    val stdDevAbsTol = expectedStdDev * stdDevTol
    var values = new ArrayBuffer[Double](nbValues)
    
    // First pass to initialize the values
    for( i <- 0 until nbValues ) {
      values += Math.abs( randomGaussian(expectedMean, expectedStdDev) )
    }
    
    val centerIdx = (nbValues / 2) - 1
    
    //println("expectedStdDev",expectedStdDev)
    
    var curStdDev = Math.sqrt( StatUtils.variance(values.toArray) )
    
    val minValue = expectedMean - 3 * expectedStdDev
    val maxValue = expectedMean + 3 * expectedStdDev
    
    //println("optimizing curStdDev")
    var i = 0
    while( Math.abs(curStdDev - expectedStdDev) > stdDevAbsTol ) {
      i += 1
      
      if( i == maxIterations ) {
        return null
      }
      //println("curStdDev",curStdDev)
      
      val sortedValues = values.sorted
      
      // If curStdDev is too high
      values = if( curStdDev > expectedStdDev ) {
        //println(s"curStdDev $curStdDev is too high")
        sortedValues.tail
      // Else curStdDev is too low
      } else {
        //println(s"curStdDev $curStdDev is too low ")
        sortedValues.remove(centerIdx)
        sortedValues
      }
      assert( values.length == nbValues - 1)
      
      // Generate a new value that must be between minValue and maxValue
      var isValueInAcceptableRange = false
      while( isValueInAcceptableRange == false ) {
        val value = Math.abs( randomGaussian(expectedMean, expectedStdDev) )
        if( value > minValue && value < maxValue ) {
          values += value
          isValueInAcceptableRange = true
        }
      }
      
      curStdDev = Math.sqrt( StatUtils.variance(values.toArray) )
    }
    
    // Center the generated values around the expectedMean
    val currentMean = values.sum / nbValues
    val centeredValues = values.map( _ * expectedMean / currentMean )
    
    centeredValues.toArray
  }
  
}