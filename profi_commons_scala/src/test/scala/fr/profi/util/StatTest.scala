package fr.profi.util

import scala.util.Random
import scala.runtime.ScalaRunTime.stringOf

import org.junit.Assert._
import org.junit.Test

import fr.profi.util.stat._

@Test
class StatTest {
  
  @Test
  def testIntHistogram {
    
    val intValues = Array(1,2,2,3,3,3,4,4,4,4,5,5,5,5,5)
    
    val histogramComputer = new EntityHistogramComputer[Int](intValues, i => i.toDouble )
    val histo = histogramComputer.calcHistogram()
    //println( stringOf(histo))
    
    // Check the number of bins
    assertEquals( 3, histo.length )
    
    // Check the bins boundaries
    assertEquals( histo(0)._1.upperBound, histo(1)._1.lowerBound, 1e-5 )
    assertEquals( histo(1)._1.upperBound, histo(2)._1.lowerBound, 1e-5 )
    
    // Check the bins content
    assertEquals( 3, histo(0)._2.length )
    assertEquals( 3, histo(1)._2.length )
    assertEquals( 9, histo(2)._2.length )
  }
  
  @Test
  def testObjectHistogram {
    
    case class User( name: String, age: Int )    
    val users = Array( User( "Bob", 45 ), User( "Kevin", 12 ), User("Henri",40), User("Lea",16), User("Tom",13) )
    
    val histogramComputer = new EntityHistogramComputer[User](users, u => u.age)
    val histo = histogramComputer.calcHistogram(nbins = 2)
    //println( stringOf(histo))
    
    // Check the number of bins
    assertEquals( 2, histo.length )
    
    // Check the bins boundaries
    assertEquals( histo(0)._1.upperBound, histo(1)._1.lowerBound, 1e-5 )
    
    // Check the bins content
    assertEquals( 3, histo(0)._2.length )
    assertEquals( 2, histo(1)._2.length )
  }
  
  @Test
  def testRandomDoubleHistogram {
    
    val randomGen = new Random() 
    val doubleValues = (1 to 10) map { i => randomGen.nextGaussian }
    
    val histogramComputer = new EntityHistogramComputer[Double](doubleValues, d => d)
    val histo = histogramComputer.calcHistogram(nbins = 4)
    //println( stringOf(histo))
    
    // Check the number of bins
    assertEquals( 4, histo.length )
    
    // Check the bins boundaries
    assertEquals( histo(0)._1.upperBound, histo(1)._1.lowerBound, 1e-5 )
    assertEquals( histo(1)._1.upperBound, histo(2)._1.lowerBound, 1e-5 )
    assertEquals( histo(2)._1.upperBound, histo(3)._1.lowerBound, 1e-5 )
    
    // Check the bins content
    val expectedSum = doubleValues.reduceLeft(_+_)
    val binContentSum = histo.foldLeft(0.0)( (s,b) => s + (if( b._2.isEmpty ) 0 else b._2.reduceLeft(_+_)) )
    assertEquals( expectedSum, binContentSum, 1e-5 )

  }
  
}