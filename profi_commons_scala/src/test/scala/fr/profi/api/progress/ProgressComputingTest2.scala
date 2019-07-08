package fr.profi.api.progress

import org.junit.Assert._
import org.junit.Test

class ProgressComputingTest2 {
 
  @Test
  def testFakedService {
    new ServiceFake2
  }
  
}

private class ServiceFake2 extends ProgressComputing {
  
  final case object STEP1 extends IProgressStepIdentity {
    val stepDescription = "first step"
  }
  
  final case object STEP2 extends IProgressStepIdentity {
    val stepDescription = "second step"
  }
  
  trait ServiceFakeSequence2 extends IProgressPlanSequence
  val progressPlan = ProgressPlan[ServiceFakeSequence2](
    name = "service fake progression",
    steps = Seq(
      ProgressStep( STEP1, maxCount = 6, weight = 1),
      ProgressStep( STEP2, maxCount = 2, weight = 1)
    )
  )
  
  this.progressComputer.registerOnProgressUpdatedAction { (identity, newProgress) =>
    println( "Step identity : " + identity.stepName + " progress = "+newProgress )
  }
  
  incrementCurrentProgressStepCount()
  incrementCurrentProgressStepCount()
  incrementCurrentProgressStepCount()
  incrementCurrentProgressStepCount(3)
  setCurrentProgressStepAsCompleted() // step1 completed
  
  val subService = new SubServiceFake2()  
  this.progressPlan(STEP2).setProgressUpdater(subService.progressComputer.getOnStepCompletedListener())
  
  subService.doStuffs
  setCurrentProgressStepAsCompleted() // step2 completed
  
  //>>|| // all steps completed
  
  // Check we have completed the progress plan
  assertEquals( "invalid progression at the end of the service", 1.0, getUpdatedProgress(), 0.0 )    
}

private class SubServiceFake2 extends ProgressComputing {
  
  final case object SUB_STEP1 extends IProgressStepIdentity {
    val stepDescription = "first sub step"
  }
  
  final case object SUB_STEP2 extends IProgressStepIdentity {
    val stepDescription = "second sub step"
  }
  
  final case object SUB_STEP3 extends IProgressStepIdentity {
    val stepDescription = "third sub step"
  }
  
  trait SubServiceFakeSequence2 extends IProgressPlanSequence
  val progressPlan = ProgressPlan[SubServiceFakeSequence2](
    name = "service fake progression",
    steps = Seq(
      ProgressStep( SUB_STEP1 ),
      ProgressStep( SUB_STEP2 )
    )
  )
  
  def doStuffs {
    
    val step1MaxCount = 101
    val step2MaxCount = 101
    this.progressPlan(SUB_STEP1).setMaxCount(step1MaxCount)
    this.progressPlan(SUB_STEP2).setMaxCount(step2MaxCount)
    
    for( i1 <- 1 to step1MaxCount ) incrementCurrentProgressStepCount()
    for( i2 <- 1 to step2MaxCount ) incrementCurrentProgressStepCount()
    
  }

}