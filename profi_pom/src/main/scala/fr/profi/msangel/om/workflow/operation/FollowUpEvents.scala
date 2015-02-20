package fr.profi.msangel.om.workflow.operation

trait IFollowUpEvent //won't mess up serialization?

/** Send a notification by e-mail */
case class EMailNotification(emailAdress: String) extends IFollowUpEvent {
  def execute(): String = null
  //  def getAsStrings() = ("Email notification at", emailAdress)
  override def toString() = emailAdress
}

/** Execute a command line */
case class CmdLineExecution(cmdLine: String) extends IFollowUpEvent {
  def execute(): String = null
  override def toString() = cmdLine
}

/** Call a web service */
case class WebServiceCall(
  url: String,
  httpMethod: String,
  body: Option[String]
) extends IFollowUpEvent {
  def execute(): String = null
  override def toString() = url
}