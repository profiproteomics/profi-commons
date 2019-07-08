package fr.profi.util.metrics

import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics

class Metric(val name: String) {

  private val counters = collection.mutable.Map[String, Integer]()
  private val counters_lock = new Object()
  
  private val summaries = collection.mutable.Map[String, SummaryStatistics]()
  private val summaries_lock = new Object()
  
  private val statistics = collection.mutable.Map[String, DescriptiveStatistics]()
  private val statistics_lock = new Object()
  
  def getCounter(name: String) = counters_lock.synchronized {
    counters.getOrElseUpdate(name, 0)
  }
  
  def setCounter(name: String, value: Integer) = counters_lock.synchronized {
   counters(name) =  value   
  }
  
  def incr(name: String) = counters_lock.synchronized {
   counters(name) = counters.getOrElseUpdate(name, 0) + 1
  }

  def decr(name: String) = counters_lock.synchronized {
   counters(name) = counters.getOrElseUpdate(name, 0) - 1
  }

  def addValue(name: String, d: Double) = summaries_lock.synchronized {
	 summaries.getOrElseUpdate(name, new SummaryStatistics).addValue(d)
  }
  
  // DBO: The API is ambiguous => it is not straightforward to understand the difference between addValue and storeValue
  // Summary is a subset of descriptive stats => why not compute DescriptiveStatistics systematically ?
  def storeValue(name: String, d: Double) = statistics_lock.synchronized {
	 statistics.getOrElseUpdate(name, new DescriptiveStatistics).addValue(d)
  }

  override def toString() = {

    val strBuilder = StringBuilder.newBuilder
    strBuilder ++= s"$name\n"
    strBuilder ++= "## Counters ##\n"

    for ((k,v) <- counters) {
      strBuilder ++= s"$k = $v\n"
    }

    strBuilder ++= "## Statistical Summaries ##\n"

    for ((k,v) <- summaries) {
      strBuilder ++= s"$k.N = ${v.getN}\n"
      strBuilder ++= s"$k.min = ${v.getMin}\n"
      strBuilder ++= s"$k.max = ${v.getMax}\n"
      strBuilder ++= s"$k.mean = ${v.getMean}\n"
      strBuilder ++= s"$k.geom_mean = ${v.getGeometricMean}\n"
      strBuilder ++= s"$k.standard_deviation = ${v.getStandardDeviation}\n"
    }

    for ((k,v) <- statistics) {
      strBuilder ++= s"$k.N = ${v.getN}\n"
      strBuilder ++= s"$k.min = ${v.getMin}\n"
      strBuilder ++= s"$k.max = ${v.getMax}\n"
      strBuilder ++= s"$k.mean = ${v.getMean}\n"
      strBuilder ++= s"$k.25 = ${v.getPercentile(25)}\n"
      strBuilder ++= s"$k.median = ${v.getPercentile(50)}\n"
      strBuilder ++= s"$k.75 = ${v.getPercentile(75)}\n"
      strBuilder ++= s"$k.skewness = ${v.getSkewness}\n"
      strBuilder ++= s"$k.kurtosis = ${v.getKurtosis}\n"
      strBuilder ++= s"$k.standard_deviation = ${v.getStandardDeviation}\n"
    }

    strBuilder.toString
  }
}