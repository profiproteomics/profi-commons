package fr.proline.api.progress

import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.TreeMap

/**
 * The Trait ProgressComputing.
 *
 * @author David Bouyssie
 */
trait ProgressComputing {
  
  /**
   * Progress plan.
   *
   * @return the progress plan
   */
  val progressPlan: ProgressPlan[_ <: IProgressPlanSequence]
  
  /**
   * Progress computer.
   *
   * @return the progress computer
   */
  lazy val progressComputer = new ProgressComputer( progressPlan )
  
  /**
   * Gets the updated progress.
   * @return A number that specifies how much of the task has been completed
   */
  def getUpdatedProgress() = progressComputer.getUpdatedProgress()

  /**
   * Increments current progress step count with a given value.
   *
   * @param count the count
   * @return the new count value
   */
  def incrementCurrentProgressStepCount( count: Int = 1 ): Int = {
    val curStep = this.progressComputer.getCurrentStep()
    require( curStep.isCompleted == false, "current step is already completed")
    
    curStep.incrementAndGetCount(count)
  }
  
  /**
   * Sets the current progress step as completed.
   */
  def setCurrentProgressStepAsCompleted(): Unit = {
    this.progressComputer.getCurrentStep().setAsCompleted()
  }
  
  /**
   * Sets the progress plan as completed.
   */
  def setProgressPlanAsCompleted(): Unit = {
    this.progressComputer.setAsCompleted()
  }
  
  /**
   * Gets the updated progress (alias of getUpdatedProgress).
   * @return A number that specifies how much of the task has been completed
   */
  def >>? = this.getUpdatedProgress()
  
  /**
   * Increments current progress step count with +1 (alias of incrementCurrentProgressStepCount).
   *
   * @return the new count value
   */
  def >>++ = this.incrementCurrentProgressStepCount(1)
  
  /**
   * Increments current progress step count with a given value (alias of incrementCurrentProgressStepCount).
   *
   * @param count the count
   * @return the new count value
   */
  def >>+( count: Int ): Int = this.incrementCurrentProgressStepCount(count)
  
  /**
   * Sets the current progress step as completed (alias of setCurrentProgressStepAsCompleted).
   */
  def >>| = this.setCurrentProgressStepAsCompleted()
  
  /**
   * Sets the progress plan as completed (alias of setProgressPlanAsCompleted).
   */
  def >>|| = this.setProgressPlanAsCompleted()
  
}