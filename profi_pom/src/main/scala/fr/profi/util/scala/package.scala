package fr.profi.util

import org.joda.time.DateTime


package object scala {

  type BigDecimalRange = (Option[BigDecimal], Option[BigDecimal])

  /** Sort joda.DateTime **/
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)


  
}