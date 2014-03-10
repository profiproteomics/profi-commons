package fr.proline.api.progress

import scala.collection.mutable.ArrayBuffer
import scala.collection.immutable.TreeMap

trait ProgressComputing {
  
  val progressPlan: ProgressPlan[_ <: IProgressPlanSequence]
  lazy val progressComputer = new ProgressComputer( progressPlan )

  protected def incrementCurrentProgressStepCount( count: Int = 1 ): Int = {
    val curStep = this.progressComputer.getCurrentStep()
    require( curStep.isCompleted == false, "current step is already completed")
    
    curStep.incrementAndGetCount(count)
  }
  
  protected def setCurrentProgressStepAsCompleted(): Unit = {
    this.progressComputer.getCurrentStep().setAsCompleted()
  }
  
  protected def setProgressPlanAsCompleted(): Unit = {
    this.progressComputer.setAsCompleted()
  }
  
  /**
   * Gets the progression.
   * @return A number that specifies how much of the task has been completed
   */
  def >>? = progressComputer.getUpdatedProgress()
  
  def >>++ = this.incrementCurrentProgressStepCount(1)
  
  def >>+( count: Int ): Int = this.incrementCurrentProgressStepCount(count)
  
  def >>| = this.setCurrentProgressStepAsCompleted()
  
  def >>|| = this.setProgressPlanAsCompleted()
  
}