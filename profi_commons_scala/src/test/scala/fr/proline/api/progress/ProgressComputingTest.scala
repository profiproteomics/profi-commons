package fr.proline.api.progress

import org.junit.Assert._
import org.junit.Test
import scala.collection.immutable.TreeMap

class ProgressComputingTest {
 
  @Test
  def testFakedService {
    new ServiceFake
  }
  
}

private class ServiceFake extends ProgressComputing {
  
  final case object STEP1 extends IProgressStepIdentity {
    val stepDescription = "first step"
  }
  
  final case object STEP2 extends IProgressStepIdentity {
    val stepDescription = "second step"
  }
  
  trait ServiceFakeSequence extends IProgressPlanSequence
  val progressPlan = ProgressPlan[ServiceFakeSequence](
    name = "service fake progression",
    steps = Seq(
      ProgressStep( STEP1, maxCount = 6, weight = 2),
      ProgressStep( STEP2, maxCount = 2, weight = 1)
    )
  )
  
  /*this.progressComputer.registerOnProgressUpdatedAction { newProgress =>
    println( "newProgress : "+newProgress )
  }*/
  >>| // step1 completed
  
  val subService = new SubServiceFake()  
  //this.progressPlan(STEP2).setProgressUpdater(subService.progressComputer.getOnStepCompletedListener())
  
  subService.doStuffs
  >>| // step2 completed
  
  //>>|| // all steps completed
  
  // Check we have completed the progress plan
  assertEquals( "invalid progression at the end of the service", 1.0, >>?, 0.0 )    
}

private class SubServiceFake extends ProgressComputing {
  
  final case object STEP1 extends IProgressStepIdentity {
    val stepDescription = "first step"
  }
  
  final case object STEP2 extends IProgressStepIdentity {
    val stepDescription = "second step"
  }
  
  trait SubServiceFakeSequence extends IProgressPlanSequence
  val progressPlan = ProgressPlan[SubServiceFakeSequence](
    name = "service fake progression",
    steps = Seq(
      ProgressStep( STEP1 ),
      ProgressStep( STEP2 )
    )
  )
  
  def doStuffs {
    
    val step1MaxCount = 101
    val step2MaxCount = 101
    this.progressPlan(STEP1).setMaxCount(step1MaxCount)
    this.progressPlan(STEP2).setMaxCount(step2MaxCount)
    
    for( i1 <- 1 to step1MaxCount ) >>++
    for( i2 <- 1 to step2MaxCount ) >>++
  }

}