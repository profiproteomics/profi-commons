package fr.profi.util

import scala.collection.mutable.ArrayBuffer
import scala.math.{floor,sqrt}

package object stat {
  
  // Inspired by: https://github.com/geetduggal/growcode/blob/master/src/main/scala/Histogram.scala
  class EntityHistogramComputer[T]( val entities: Seq[T], val valueExtractor: T => Double ) {
    
    case class Bin( center: Double, lowerBound: Double, upperBound: Double )

    def calcHistogram( nbins: Int = sqrt(entities.length).toInt, range: Option[(Double,Double)] = None ): Array[Pair[Bin,Seq[T]]] = {
    
      val sortedEntities = entities.sortBy( e => valueExtractor(e) )
      val( lowestEntity, highestEntity ) = (sortedEntities.head,sortedEntities.last)
      
      val minVal = valueExtractor(lowestEntity)
      val b = 1e-10
      
      // The bin width. Simply the bounds of the histogram divided by the number of bins
      val binSize = {
        if ( range.isDefined ) (range.get._2 - range.get._1) / nbins
        else ( valueExtractor(highestEntity) - minVal ) / nbins
      } + b // small hack in order to be sure all values are included in the histogram range
      
      def binIndex( v: Double ): Int = { floor((v - minVal) / (binSize) ).toInt }
      def bin( idx: Int): Bin = {
        val lowerBound = minVal + ( idx * (binSize) )
        Bin(
          center = lowerBound + (0.5 * binSize),
          lowerBound = lowerBound,
          upperBound = lowerBound + binSize
        )
      }
      
      // Array of values for the histogram bins
      val bins = Array.tabulate[ Pair[Bin,ArrayBuffer[T]] ](nbins) { i =>
        Pair( bin(i),new ArrayBuffer[T])
      }
      
      // For each value, compute the bin in which it resides and
      // appropriately update that bin's frequency (weight)
      entities.foreach { e =>
        val binIdx = binIndex( valueExtractor(e) )
        bins( binIdx )._2 += e
      }
      
      bins.map( p => p._1 -> p._2.toSeq )  
    }
  
  }


}