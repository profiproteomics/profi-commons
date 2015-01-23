package fr.profi.msangel.om.workflow.operation

/** Send a notification by e-mail */
case class EMailNotification(emailAdress: String) {
  def execute(): String = null
  //  def getAsStrings() = ("Email notification at", emailAdress)
  override def toString() = emailAdress
}

/** Execute a command line */
case class CmdLineExecution(cmdLine: String) {
  def execute(): String = null
  override def toString() = cmdLine
}

/** Call a web service */
case class WebServiceCall(
  url: String,
  httpMethod: String,
  body: Option[String]
) {
  def execute(): String = null
  override def toString() = url
}