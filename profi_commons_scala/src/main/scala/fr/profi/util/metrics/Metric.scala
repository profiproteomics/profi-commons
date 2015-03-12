package fr.profi.util.metrics

import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

class Metric(val name: String) {

  val counters = collection.mutable.Map[String, Integer]()
  val summaries = collection.mutable.Map[String, SummaryStatistics]()
  val statistics = collection.mutable.Map[String, DescriptiveStatistics]()
  
  def setCounter(name: String, value: Integer) = {
   counters(name) =  value   
  }
  
  def getCounter(name: String) = {
   counters.getOrElseUpdate(name, 0)   
  }
  
  def incr(name: String) = {
   counters(name) = counters.getOrElseUpdate(name, 0) + 1
  }

  def decr(name: String) = {
   counters(name) = counters.getOrElseUpdate(name, 0) - 1
  }

  def addValue(name: String, d: Double) = {
	 summaries.getOrElseUpdate(name, new SummaryStatistics).addValue(d)
  }
  
  def storeValue(name: String, d: Double) = {
	 statistics.getOrElseUpdate(name, new DescriptiveStatistics).addValue(d)
  }
    
  override def toString() = {
    val strBuilder = StringBuilder.newBuilder
    strBuilder.append(name).append("\n")
    strBuilder.append("## Counters ##").append("\n")    
    for (c <- counters) {
      strBuilder.append(c._1).append(" = ").append(c._2).append('\n')
    }
    strBuilder.append("## Statistical Summaries ##").append("\n")    
    for (c <- summaries) {
      strBuilder.append(c._1).append(".N = ").append(c._2.getN()).append('\n')
      strBuilder.append(c._1).append(".min = ").append(c._2.getMin()).append('\n')
      strBuilder.append(c._1).append(".max = ").append(c._2.getMax()).append('\n')
      strBuilder.append(c._1).append(".mean = ").append(c._2.getMean()).append('\n')
      strBuilder.append(c._1).append(".geomMean = ").append(c._2.getGeometricMean()).append('\n')
      strBuilder.append(c._1).append(".standard_deviation = ").append(c._2.getStandardDeviation()).append('\n')
    }
     for (c <- statistics) {
      strBuilder.append(c._1).append(".N = ").append(c._2.getN()).append('\n')
      strBuilder.append(c._1).append(".min = ").append(c._2.getMin()).append('\n')
      strBuilder.append(c._1).append(".max = ").append(c._2.getMax()).append('\n')
      strBuilder.append(c._1).append(".mean = ").append(c._2.getMean()).append('\n')
      strBuilder.append(c._1).append(".25 = ").append(c._2.getPercentile(25)).append('\n')      
      strBuilder.append(c._1).append(".median = ").append(c._2.getPercentile(50)).append('\n')
      strBuilder.append(c._1).append(".75 = ").append(c._2.getPercentile(75)).append('\n')
      strBuilder.append(c._1).append(".skewness = ").append(c._2.getSkewness()).append('\n')
      strBuilder.append(c._1).append(".kurtosis = ").append(c._2.getKurtosis()).append('\n')
      strBuilder.append(c._1).append(".standard_deviation = ").append(c._2.getStandardDeviation()).append('\n')
     }
    strBuilder.toString
  }
}