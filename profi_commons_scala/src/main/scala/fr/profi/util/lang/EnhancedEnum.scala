package fr.profi.util.lang

trait EnhancedEnum extends Enumeration {
  lazy val valueByKey = values.map(v => (v.toString, v)).toMap
  
  def contains(s: String): Boolean = valueByKey.contains(s)
  
  def maybeNamed(s: String): Option[this.type#Value] = valueByKey.get(s)
  
  implicit def enumToString(value: this.type#Value): String = value.toString
}