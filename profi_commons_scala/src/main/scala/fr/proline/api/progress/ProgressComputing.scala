package fr.proline.api.progress

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap

/**
 * The Object singleton ProgressComputingConfig.
 *
 * @author David Bouyssie
 */
object ProgressExecutionProfiling {
  
  private var _profileExecution = false
  private var _executionTimesByStepIdentity = new HashMap[IProgressStepIdentity,ArrayBuffer[Long] ]()
  
  def enable() = synchronized { _profileExecution = true }
  def disable() = synchronized { _profileExecution = false }
  def isEnabled() = _profileExecution
  
  def addExecutionTime( stepIdentity: IProgressStepIdentity, execTime: Long ) = synchronized {
    _executionTimesByStepIdentity.getOrElseUpdate(stepIdentity, new ArrayBuffer[Long]() ) += execTime
  }
  
  def getExecutionStatistics(): Map[IProgressStepIdentity,(Long,Long)] = {

    ( for ( (stepIdentity,execTimes) <- _executionTimesByStepIdentity ) yield {
      val execTimeSum = execTimes.sum
      val execTimeAvg = execTimeSum / execTimes.length
      stepIdentity -> (execTimeSum,execTimeAvg)
    } ) toMap
  }
  
  def logExecutionStatistics( logger: com.typesafe.scalalogging.slf4j.Logger ) {
    
    val execStats = this.getExecutionStatistics
    val sortedStepIdentities = execStats.keys.toList.sortBy(_.stepName)
    
    for( stepIdentity <- sortedStepIdentities ) {
      val (totalExcTime,avgExecTime) = execStats(stepIdentity)
    
      logger.debug( s"execution statistics of step ${stepIdentity.stepName} (${stepIdentity.stepDescription}): " )
      logger.debug( s"- total exec time = $totalExcTime ms" )
      logger.debug( s"- average exec time = $avgExecTime ms" )
    }
  }
  
}

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
    this.progressComputer.setCurrentStepAsCompleted()
  }
  
  /*
  /**
   * Sets the progress plan as completed.
   */
  /*def setProgressPlanAsCompleted(): Unit = {
    this.progressComputer.setAsCompleted()
  }*/
  
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
  //def >>|| = this.setProgressPlanAsCompleted()
  */
  
}