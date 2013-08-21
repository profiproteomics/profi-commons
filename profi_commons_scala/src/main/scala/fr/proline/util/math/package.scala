package fr.proline.util

/** Miscellaneous helpers */
package object math {

  /** Computes the median value of a sequence of Doubles */
  /*def median(s: Seq[Double]) = {
    val (lower, upper) = s.sortWith(_<_).splitAt(s.size / 2)
    if (s.size % 2 == 0) (lower.last + upper.head) / 2.0 else upper.head
  }*/
  
  def median[T](s: Seq[T])(implicit n: Fractional[T]) = {
    import n._
    val (lower, upper) = s.sortWith(_<_).splitAt(s.size / 2)
    if (s.size % 2 == 0) (lower.last + upper.head) / fromInt(2) else upper.head
  }
  
  def getMedianObject[T]( objects: List[T], sortingFunc: Function2[T,T,Boolean] ): T = {
  
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
  
  /** Interpolate a value using the slope and intercept of a line
   *  estimated with two consecutive data points coordinates
   *  in the provided Pair[Double,Double] vector.
   **/
  def interpolateValue( index: Int, xValue: Float, xyValues: Seq[Pair[Double,Double]] ): Double = {
    require( index >= -1 && index < xyValues.length, "index is out of range" )
    
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
  
  /** Interpolate a value using the slope and intercept of a line
   *  estimated with two consecutive data points coordinates
   *  in the provided Pair[Float,Float] vector.
   **/
  def interpolateValue( index: Int, xValue: Float, xyValues: Seq[Pair[Float,Float]] ): Float = {
    require( index >= -1 && index < xyValues.length, "index is out of range" )
    
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
        (a * xValue + b).toFloat
      }
    }
 
  }
  
}
