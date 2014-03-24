package fr.proline.api.progress

import scala.collection.mutable.ArrayBuffer
import scala.reflect.ClassTag

class ProgressComputer[S <: IProgressPlanSequence]( private val progressPlan: ProgressPlan[S] )(implicit seqTag: ClassTag[S]) {
  require( progressPlan != null, "progressPlan is null")
  
  def getOnProgressUpdatedListener() = {
    val progressComputer = this
    new Object with IProgressUpdatedListener {
      def listenOnProgressUpdatedAction( action: Float => Unit ) = {
        progressComputer.registerOnProgressUpdatedAction(action)
      }
    }
  }
  
  def getOnStepCompletedListener() = {
    val progressComputer = this
    new Object with IStepCompletedListener {
      def listenOnStepCompletedAction( action: Float => Unit ) = {
        progressComputer.registerOnStepCompletedAction(action)
      }
    }
  }
  
  private var _isCompleted = false
  
  // A number that specifies how much of the task has been completed
  private var _progress = 0f
  private val _onProgressUpdatedActions = new ArrayBuffer[Float => Unit](0)
  private val _onStepCompletedActions = new ArrayBuffer[Float => Unit](0)
  
  // Watch for progress update and step completion
  for( step <- this.getSteps() ) {    
    step.registerOnProgressUpdatedAction { newProgress =>
      if( _onProgressUpdatedActions.isEmpty == false ) {
        this.updateProgress()
        
        // Execute on progress updated registered actions
        for( action <- _onProgressUpdatedActions ) action(this._progress)
      }
      
    }
    step.registerOnStepCompletedAction { () =>
      
      if( _onStepCompletedActions.isEmpty == false ) {
        this.updateProgress()
        
        // Execute on progress updated registered actions
        for( action <- _onStepCompletedActions ) action(this._progress)
      }
    }
  }
  
  def setAsCompleted(): this.type = synchronized {
    this._progress = 1f
    this._isCompleted = true
    this
  }

  def getSteps(): Seq[ProgressStep[S]] = progressPlan.steps
  
  def getCurrentStep(): ProgressStep[S] = {
    val allSteps = this.getSteps
    allSteps.find( _.isCompleted == false ).getOrElse(allSteps.last)
  }
  
  def getNumberOfCompletedSteps(): Int = { 
    if( this._isCompleted ) return this.progressPlan.getNumberOfSteps 
    
    this.getSteps.count( _.isCompleted )
  }
  
  /**
   * Gets the progression.
   * @return A number that specifies how much of the task has been completed
   */
  def getProgress(): Float = {
    this._progress
  }
  
  def getUpdatedProgress(): Float = {
    this.updateProgress()
    this._progress
  }
  
  protected def updateProgress(): Unit = synchronized {
    if( this._isCompleted ) return ()
    
    val steps = this.getSteps
    
    // Sum the weights of all defined steps
    val weightSum = steps.foldLeft(0f) { (sum,step) => sum + step.weight }
    
    // Sum the progressions of all defined steps
    val progressSum = steps.foldLeft(0f) { (sum,step) =>
      sum + step.weight * step.getProgress
    }
    
    // Compute the average progression
    val averageProgress = progressSum / weightSum
    
    if( averageProgress >= 1 ) {
      this.setAsCompleted()
      this._progress = 1f
    } else
      this._progress = averageProgress
    
  }
  
  def registerOnProgressUpdatedAction( action: Float => Unit ) = {
    _onProgressUpdatedActions += action
  }
  
  def registerOnStepCompletedAction( action: Float => Unit ) = {
    _onStepCompletedActions += action
  }
  
  /*method getProgressAsString() {
    val nbSteps = this.numOfSteps
    val nbCompletedSteps = this.getNumOfCompletedSteps
    val totalProgress = this.getProgress
    
    val status = "nbCompletedSteps/nbSteps completed step(s) for a total progress of ".
                 sprintf("%.1f",totalProgress*100) .'%'
    
    return status
  }*/

}