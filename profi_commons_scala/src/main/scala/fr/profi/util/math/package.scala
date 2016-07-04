package fr.profi.util

import fr.profi.util.primitives.isZeroOrNaN
import scala.collection.mutable.ArrayBuffer
import org.apache.commons.math3.stat.StatUtils
import fr.profi.util.random.randomGaussian

/** Miscellaneous helpers */
package object math {

  /** Computes the median value of a sequence of Doubles */
  /*def median(s: Seq[Double]): Double = {
    val (lower, upper) = s.sortWith(_<_).splitAt(s.size / 2)
    if (s.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
  }
  
  def median(s: Seq[Float]): Float = {
    val (lower, upper) = s.sortWith(_<_).splitAt(s.size / 2)
    if (s.size % 2 == 0) (lower.last + upper.head) / 2.0f else upper.head
  }*/
  
  def filteredMean(s: Array[Float]): Float = {
    val defS = s.filter( isZeroOrNaN(_) == false )
    if( defS.isEmpty ) Float.NaN else defS.sum / defS.length
  }
  
  def filteredMedian(s: Array[Float]): Float = {
    val defS = s.filter( isZeroOrNaN(_) == false )
    if( defS.isEmpty ) Float.NaN else median(defS)
  }
  
  def median[T](s: Seq[T])(implicit n: Fractional[T]): T = {
    import n._
    val (lower, upper) = s.sortBy(x=>x).splitAt(s.size / 2)
    if (s.size % 2 == 0) (lower.last + upper.head) / fromInt(2) else upper.head
  }
  
  def getMedianObject[T]( objects: Seq[T], sortingFunc: Function2[T,T,Boolean] ): T = {
  
    val sortedObjects = objects.sortWith { (a,b) => sortingFunc(a,b) } 
    val nbObjects = sortedObjects.length
    
    // Compute median index
    var medianIndex = 0
    
    // If even number of objects
    if( nbObjects % 2 == 0 ) medianIndex = nbObjects / 2
    // Else => odd number of objects
    else medianIndex = (nbObjects-1) / 2
    
    sortedObjects(medianIndex)
    
  }  
  
  /*object factorizeSeq {
    def apply (seq : Seq[_]) : Array[Pair[_, _]] = {
      val p = new scala.collection.mutable.ArrayBuffer[Pair[_, _]]()
      for (e1 <- seq) for (e2 <- seq) p += Pair(e1, e2)
      p.toArray
    }
  }*/
  
  object combinations {
 
    def apply[A](n: Int, ls: List[A]): List[List[A]] =
      if (n == 0) List(Nil)
      else flatMapSublists(ls) { sl =>
        apply(n - 1, sl.tail) map {sl.head :: _}
      }
    
    // flatMapSublists is like list.flatMap, but instead of passing each element
    // to the function, it passes successive sublists of L.
    private def flatMapSublists[A,B](ls: List[A])(f: (List[A]) => List[B]): List[B] = 
      ls match {
        case Nil => Nil
        case sublist@(_ :: tail) => f(sublist) ::: flatMapSublists(tail)(f)
      }
  
  }
  
  /** Compute slope and intercept of a line using two data points coordinates */
  def calcLineParams( x1: Double, y1: Double, x2: Double, y2: Double ): Tuple2[Double,Double] =  {
    
    val deltaX = x2 - x1
    require( deltaX != 0, "can't solve line parameters with two identical x values (" + x1 + ")" )
    
    val slope = (y2 - y1) / deltaX
    val intercept = y1 - (slope * x1)
    
    ( slope, intercept )
  }
  
  /** 
   * Interpolates a value using the slope and intercept of a line
   * estimated with two consecutive data points coordinates
   * in the provided Pair[Double,Double] vector.
   * Assumes that XY values are already sorted by X.
   * If fixOutOfRange is true then out of range values will be replaced by first or last XY pair of the vector.
   *  
   * @param xValue the X value
   * @param xyValues the XY values
   * @paramm fixOutOfRange enable/disable the support for out of range values
   * @return the interpolated Y value
   */
  // TODO: create a LinearInterpolator class which allows to index the XY vector (faster lookup) => use EntityHistogram as backend ???
  def linearInterpolation( xValue: Double, xyValues: Seq[Pair[Double,Double]], fixOutOfRange: Boolean ): Double = {
    
    var index = xyValues.indexWhere( _._1 >= xValue )
    if( index == -1 ) {
      if( !fixOutOfRange ) throw new IllegalArgumentException("index is out of range")
      else index = if( xValue < xyValues.head._1 ) 0 else -1
    }
    
    // If we are looking at the left-side of the vector boundaries
    // then we take the Y value of the first element
    if( index == 0  ) xyValues.head._2
    // Else if we are looking at the right-side of the vector boundaries
    // then we take the Y of the last element
    else if( index == -1 ) xyValues.last._2
    // Else we are inside the vector boundaries
    // We then compute the linear interpolation
    else {
      val( x1, y1 ) = xyValues(index-1)
      val( x2, y2) = xyValues(index)
      
      // If the vector contains two consecutive values with a same X coordinate
      // Then we take the mean of the corresponding Y values
      if( x1 == x2 ) (y1 + y2)/2
      // Else we compute the linear interpolation
      else {
        val ( a, b ) = calcLineParams( x1, y1, x2, y2 )
        (a * xValue + b)
      }
    }
 
  }
  
  def linearInterpolation( xValue: Double, xyValues: Seq[Pair[Double,Double]] ): Double = {
    linearInterpolation(xValue,xyValues,true)
  }
  
  /** 
   * Interpolates a value using the slope and intercept of a line
   * estimated with two consecutive data points coordinates
   * in the provided Pair[Float,Float] vector.
   * Assumes that XY values are already sorted by X.
   * If fixOutOfRange is true then out of range values will be replaced by first or last XY pair of the vector.
   *  
   * @param xValue the X value
   * @param xyValues the XY values
   * @paramm fixOutOfRange enable/disable the support for out of range values
   * @return the interpolated Y value
   */
  def linearInterpolation( xValue: Float, xyValues: Seq[Pair[Float,Float]], fixOutOfRange: Boolean ): Float = {
    
    var index = xyValues.indexWhere( _._1 >= xValue )
    if( index == -1 ) {
      if( !fixOutOfRange ) throw new IllegalArgumentException("index is out of range")
      else index = if( xValue < xyValues.head._1 ) 0 else -1
    }
    
    // If we are looking at the left-side of the vector boundaries
    // then we take the Y value of the first element
    if( index == 0 ) xyValues.head._2
    // Else if we are looking at the right-side of the vector boundaries
    // then we take the Y of the last element
    else if( index == -1 ) xyValues.last._2
    // Else we are inside the vector boundaries
    // We then compute the linear interpolation
    else {
      val( x1, y1 ) = xyValues(index-1)
      val( x2, y2) = xyValues(index)
      
      // If the vector contains two consecutive values with a same X coordinate
      // Then we take the mean of the corresponding Y values
      if( x1 == x2 ) (y1 + y2)/2
      // Else we compute the linear interpolation
      else {
        val ( a, b ) = calcLineParams( x1, y1, x2, y2 )
        (a * xValue + b).toFloat
      }
    }
 
  }
  
  def linearInterpolation( xValue: Float, xyValues: Seq[Pair[Float,Float]] ): Float = {
    linearInterpolation(xValue,xyValues,true)
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
