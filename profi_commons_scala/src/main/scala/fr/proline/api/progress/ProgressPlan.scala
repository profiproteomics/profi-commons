package fr.proline.api.progress

import java.util.concurrent.atomic.AtomicInteger
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.reflect.ClassTag

case class ProgressPlan[S <: IProgressPlanSequence](
  name: String,
  steps: Seq[ProgressStep[S]]
)(implicit seqTag: ClassTag[S]) {
  require( name != null, "name is null" )
  require( steps != null, "stepIdentities is null" )
  
  require(
    steps.map(_.identity).distinct.length == steps.length,
    "the progress plan must not contain duplicated step identities"
  )
  
  val stepNumbers = steps.map(_.getNumber)
  require( stepNumbers.sorted == stepNumbers, "steps must be provided in same order than construction one" )  

  private var _stepByIdent =  this.steps.map(s => s.identity -> s).toMap
  
  def get( stepIdent: IProgressStepIdentity ): Option[ProgressStep[S]] = {
    this._stepByIdent.get(stepIdent)
  }
  
  def apply( stepIdent: IProgressStepIdentity ): ProgressStep[S] = {
    val stepOpt = this.get(stepIdent)
    
    require(
      stepOpt.isDefined,
      s"this step is not registered in this progress computer"
    )
    
    stepOpt.get
  }
  
  def getNumberOfSteps() = this._stepByIdent.size
  
  def stepExists(stepIdentity: IProgressStepIdentity) = this._stepByIdent.contains(stepIdentity)
  
}

trait IProgressPlanSequence

object ProgressPlanSequences {
  private val sequenceMap = new HashMap[ClassTag[_],AtomicInteger]
  private val stepNumberSeq = new AtomicInteger(0)

  def nextStepNumber[S <: IProgressPlanSequence]()(implicit tag: ClassTag[S]): Int = synchronized {
    sequenceMap.getOrElseUpdate(tag, new AtomicInteger(0)).incrementAndGet()    
  }
}

trait IProgressStepIdentity {
  def stepDescription: String
  
  final def stepName: String = {
    val className = this.getClass().getName()
    require( className contains '$', "only object singleton can implement this trait")
    
    className.split('$')(1)
  }
  
}

/*trait IAnnotatedProgressStepIdentity extends IProgressStepIdentity {
  
  private val stepAnnotation = this.getClass().getAnnotation(classOf[ProgressStepInfo])
  require( stepAnnotation != null, "ProgressStepInfo annotation is missing")
  
  def stepDescription: String = stepAnnotation.description()
}*/

case class ProgressStep[S <: IProgressPlanSequence](
  identity: IProgressStepIdentity,
  private var maxCount: Int = 1,
  weight: Float = 1
)(implicit seqTag: ClassTag[S]) {
  
  private var _isCompleted = false
  private var _count = 0
  private var _progress = 0f
  private var _number = ProgressPlanSequences.nextStepNumber[S]()
  private val _onProgressUpdatedActions = new ArrayBuffer[Float => Unit](0)
  private val _onStepCompletedActions = new ArrayBuffer[() => Unit](0)
  
  def getNumber(implicit tag: ClassTag[S]) = _number
  def getName(implicit tag: ClassTag[S]) = this.identity.stepName
  def getDescription(implicit tag: ClassTag[S]) = this.identity.stepDescription
  def isCompleted = this._isCompleted
  
  def setAsCompleted(): ProgressStep[S] = synchronized {
    this._isCompleted = true
    
    // Execute on step updated actions
    for( action <- _onStepCompletedActions ) action()
    
    this
  }

  def getCount = this._count

  def setCount( count: Int ): ProgressStep[S] = synchronized {
    if( this.isCompleted ) return this
    
    require( count >= 0, "count must be a positive integer" )
    require( count <= maxCount, "step count can't be greater than maxCount" )
    
    this._count = if( count >= maxCount ) maxCount else count  
    
    this.setProgress( this._count.toFloat / this.maxCount )
    
    this
  }

  def incrementAndGetCount( increment: Int = 1 ): Int = {
    this.setCount( this.getCount + increment ).getCount
  }
  
  def getMaxCount = maxCount
  
  def setMaxCount( maxCount: Int ): ProgressStep[S] = synchronized {
    require( maxCount > 0, "maxCount must be a strictly positive integer" )
    require( _count == 0, "can't change the maximum count if the step is started (count > 0)" )
    
    this.maxCount = maxCount
    
    this
  }
  
  /**
   * Returns a number between 0 and 1 indicating how much of the step has been completed.
   */
  def getProgress(): Float = {
    if( this.isCompleted ) return 1f
    
    this._progress
  }
  
  def setProgress( newProgress: Float ) = synchronized {
    require( newProgress >=0 && newProgress <= 1, "progress must be a number between 0 and 1")
    
    this._progress = if( newProgress > 1 ) 1 else newProgress
    
    // Execute on progress updated actions
    for( action <- _onProgressUpdatedActions ) action( this.getProgress() )
    
    if( _progress == 1 ) this.setAsCompleted
    
    this
  }
  
  def setProgressUpdater( progressUpdater: IProgressStepListener ) = {
    progressUpdater match {
      case progressUpatedListener: IProgressUpdatedListener => {
        progressUpatedListener.registerOnProgressUpdatedAction { newProgress =>
          this.setProgress( newProgress )
        }
      }
      case stepCompletedListener: IStepCompletedListener => {  
        stepCompletedListener.registerOnStepCompletedAction { newProgress =>
          this.setProgress( newProgress )
        }
      }
    }
    
    this
  }
  
  def registerOnProgressUpdatedAction(action: Float => Unit) = synchronized {
    this._onProgressUpdatedActions += action
    this
  }
  
  def registerOnStepCompletedAction(action: () => Unit) = synchronized {
    this._onStepCompletedActions += action
    this
  }
  
}

trait IProgressStepListener
trait IProgressUpdatedListener extends IProgressStepListener {
  def registerOnProgressUpdatedAction( action: Float => Unit )
}
trait IStepCompletedListener extends IProgressStepListener {
  def registerOnStepCompletedAction( action: Float => Unit )
}
