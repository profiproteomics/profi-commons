package fr.profi.util

import org.joda.time.DateTime
import fr.profi.msangel.om.workflow.operation.tuple2Format

package object scala {

  type BigDecimalRange = (Option[BigDecimal], Option[BigDecimal])

  /** Sort joda.DateTime **/
  implicit def dateTimeOrdering: Ordering[DateTime] = Ordering.fromLessThan(_ isBefore _)

}