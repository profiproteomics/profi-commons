package fr.profi.jdbc

trait IBooleanFormatter {
  def formatBoolean( b: Boolean ): Any
}

object DefaultBooleanFormatter extends IBooleanFormatter {
  def formatBoolean( b: Boolean ): Any = b
}

object AsFullStringBooleanFormatter extends IBooleanFormatter {
  def formatBoolean( b: Boolean ): Any = {
     b match {
      case true => "true"
      case false => "false"
    }
  }
}

object AsShortStringBooleanFormatter extends IBooleanFormatter {
  def formatBoolean( b: Boolean ): Any = {
     b match {
      case true => "t"
      case false => "f"
    }
  }
}

object AsIntBooleanFormatter extends IBooleanFormatter {
  def formatBoolean( b: Boolean ): Any = {
     b match {
      case true => 1
      case false => 0
    }
  }
}