package fr.profi.util.scala

import scala.collection.mutable.ArrayBuffer

/** Utility for checking **/
class CheckResult(
  val bufferInitSize: Option[Int] = None,
  var isSuccess: Boolean = true
) {

  private val _errors = new ArrayBuffer[String](bufferInitSize.getOrElse(16))

  def isFailure: Boolean = !isSuccess
  def errors: ArrayBuffer[String] = _errors
  def getErrorString(separator: String = "\n") = errors.mkString(separator)

  def addError(error: String) = {
    setFailure()
    _errors += error
  }

  def addErrors(errors: ArrayBuffer[String]) = {
    setFailure()
    _errors ++= errors
  }
  
  def setFailure() { isSuccess = false }
  def setSuccess() { isSuccess = true }
}