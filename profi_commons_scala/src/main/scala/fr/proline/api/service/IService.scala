package fr.proline.api.service

import scala.collection.mutable.ArrayBuffer

import com.weiglewilczek.slf4s.Logging

trait IService extends Runnable with HasProgress with Logging {

  protected def beforeInterruption() = {}

  def runService(): Boolean

  // Define a run method which implements the Thread interruption policy
  def run(): Unit = {

    /* Check interrupt state before executing work */
    if (Thread.interrupted()) {
      val message = "Current thread interrupted before running IService"
      logger.warn(message)

      throw new InterruptedException(message)
    } else {

      registerOnProgressAction({

        if (Thread.interrupted()) { // Check if current thread is interrupted
          throw new InterruptedException("Current thread is interrupted")
        }

      })

      try {
        runService()
      } catch {

        case ie: InterruptedException => {
          /* Log and re-throw InterruptedException */
          logger.warn("IService.runService() interrupted", ie)

          beforeInterruption()

          throw ie
        }

      }

    } // End if (current thread not interrupted)

  }

}

trait HasProgress extends Logging {

  case class Step

  import scala.collection.mutable.ArrayBuffer

  //val progressPlan: Array[Step]

  private val progress = 0f
  private val onProgressActions = new ArrayBuffer[Function0[Unit]](0)
  //private var onStepActions = new Array[ArrayBuffer[Function0[Unit]]](progressPlan.length)

  protected def >>>(): Unit = {

    // Execute all registered actions
    for (onProgressAction <- onProgressActions) {
      onProgressAction()
    }

    // TODO: execute step actions

  }

  def registerOnProgressAction(action: => Unit): Unit = this.onProgressActions += { () => action }

  //def registerOnStepAction( stepNum: Int, action: () => Unit ): Unit = this.onStepActions(stepNum-1) += action

}