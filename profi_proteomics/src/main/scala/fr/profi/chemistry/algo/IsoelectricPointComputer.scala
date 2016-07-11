package fr.profi.chemistry.algo

import scala.collection.mutable.HashMap
import fr.profi.util.lang.EnhancedEnum
import fr.profi.chemistry.model._

object PkSymbol extends EnhancedEnum {
  val C_TERM, N_TERM, C, D, E, H, K, R, Y = Value
}

object PkType extends EnhancedEnum {
  val ACID, BASE = Value
}

// Source: http://isoelectric.ovh.org/files/isoelectric-point-theory.html
object PkTable {
  
  import PkSymbol._
  
  val Sillero = Map(C_TERM->3.2f,D->4.0f,E->4.5f,N_TERM->8.2f,K->10.4f,R->12.0f,H->6.4f,C->9.0f,Y->10.0f)
  val Rodwell = Map(C_TERM->3.1f,D->3.86f,E->4.25f,N_TERM->8.0f,K->11.5f,R->11.5f,H->6.0f,C->8.33f,Y->10.07f)
  val Lehninger = Map(C_TERM->3.1f,D->4.4f,E->4.4f,N_TERM->8.0f,K->10.0f,R->12f,H->6.5f,C->8.5f,Y->10.0f)
  val EMBOSS = Map(C_TERM->3.6f,D->3.9f,E->4.1f,N_TERM->8.6f,K->10.8f,R->12.5f,H->6.5f,C->8.5f,Y->10.1f)
  val Solomon = Map(C_TERM->2.4f,D->3.9f,E->4.3f,N_TERM->9.6f,K->10.5f,R->12.5f,H->6.0f,C->8.3f,Y->10.1f)
}

object IsoelectricPointComputer {
  
  import PkType._
  import PkSymbol._
  
  private val CHARGE_PRECISION = 0.001 // Zero charge precision
  private val PH_PRECISION = 0.01 // pH variation precision
  
  private val PK_A = 4.86f
  private val PK_B = 9.99f  
  private val partialAcidBaseMapping = Map(D->ACID,E->ACID,K->BASE,R->BASE)
  private val fullAcidSet = Set(C,D,E,Y, C_TERM)
  private val fullBaseSet = Set(H,K,R, N_TERM)
  
  implicit class NumericOps( val value: Float ) extends AnyVal {
    def **(power: Float): Float = math.pow(value, power).toFloat
    def **(power: Double): Double = math.pow(value, power)
  }
}

class IsoelectricPointComputer(
  aaTable: AminoAcidTableLike,
  pkTable: Map[PkSymbol.Value, Float] = PkTable.Lehninger
) {
  
  import IsoelectricPointComputer._
  import PkType._
  import PkSymbol._

  /**
   * Compute the pH allowing to obtain a protein with a null charge (as known as the isoelectric point).
   */
  def computePI(aaSequence: String): Float = {
    
    val abundanceMap = new AminoAcidComposition(aaSequence, aaTable).abundanceMap.map { case (aa, aaCount) =>
      aa.symbol -> aaCount
    }
    
    // Add N_TERM and C_TERM symbols to the abundanceMap
    abundanceMap(N_TERM) = 1f
    abundanceMap(C_TERM) = 1f
    
    // Compute an approximated pI value to converge to the true value
    val approxPIOpt = _computeApproximativePI( abundanceMap, minAcidBaseCount = Some(1) )
    val approxPI = if( approxPIOpt.isDefined ) approxPIOpt.get else 7
    //println("approxPI: " + approxPI)
    
    // Define a delta parameter that will be used to modify pH when charge != 0
    val delta = if( approxPIOpt.isDefined ) 1f else 4f
    
    // Optimize the approximate pI value
    _computeTheoreticalPI( abundanceMap, approxPI, delta )
  }
  
  /**
   * Approximative pI is calculated using the "regressed" theory method (method 4) described by:
   * Patrickios & Yamasaki (1995) Anal. Biochem. 231:82-91 (equation [8], page 84)
   */
  private def _computeApproximativePI(abundanceMap: HashMap[String,Float], minAcidBaseCount: Option[Int] = None): Option[Float] = {
    val acidBaseCounts = HashMap( ACID -> 1f, BASE -> 1f ) // set default counts for N-term and C-term
    
    var acidBaseCount = 0f
    for( (aa, pkType) <- partialAcidBaseMapping; aaCount <- abundanceMap.get(aa) ) {
      acidBaseCounts(pkType) += aaCount
      acidBaseCount += aaCount
    }
    if (minAcidBaseCount.isDefined && acidBaseCount < minAcidBaseCount.get) return None
    
    val R = acidBaseCounts(ACID) / acidBaseCounts(BASE)
    
    // InSilico code
    //my $pKa= 4.2;
    //my $pKb= 11.2;
    //my $pI = $pKa - log10((0.5)*((1-$R)/$R)+sqrt((1-$R)*(1-$R)/$R/$R+(4/$R)*10**($pKa-$pKb)));
    
    // Other code, SOURCE: https://lists.sdsc.edu/pipermail/pdb-l/2006-March/003232.html

    
    val pI = PK_B + math.log10( 0.5 * ( ((1 - R)/R) + math.sqrt(((1-R)/R) ** 2 + (4/R) * 10**(PK_A - PK_B)) ) )
    
    Some(pI.toFloat)
  }
  
  /** 
   * Compute the theoretical isoelectric point.
   * In order to calculate the pH where charge is 0 a loop is required.
   * The loop will start by computing the charge of the protein at pH=7,
   * and if charge is not 0, a new charge value will be computed by using a different pH.
   * This procedure is repeated until charge is 0 (at isoelectric point).
   * 
   * @param aaAbundanceMap A HashMap containing the AA counts of the analyzed protein.
   * @param pH The pH value for which the pI will be computed.
   * @param deltaParam This parameter will be used to modify pH when charge != 0.
   * @return The computed pI.
   */
  private def _computeTheoreticalPI(
    aaAbundanceMap: HashMap[String,Float],
    pH: Float = 7,
    deltaParam: Float = 4f,
    maxIterations: Int = 100
  ): Float = {

    var pI = pH.toDouble
    var delta = deltaParam // The value of delta will change during the loop
    var (previousPI, previousCharge) = (0.0, 0.0)

    // TODO: use a maxIter param
    var iterCount = 0
    while (iterCount <= maxIterations) {

      // Compute charge of protein at corresponding pH
      val charge = _computeProteinCharge(aaAbundanceMap, pI)
      //println(s"$pI $charge")

      // Check whether charge is near 0 (consequently, pH will be the isoelectric point)
      if (math.abs(charge) <= CHARGE_PRECISION) return pI.toFloat
      
      // Simple method without linear regression
      /*if (charge > 0) pI += delta else pI -= delta
      delta /= 2*/
      
      // Change pH value using a linear regression
      if (previousCharge != 0 && previousPI != 0 && charge != previousCharge) {
        val equationParams = fr.profi.util.math.calcLineParams(previousCharge, previousPI, charge, pI)
        
        previousPI = pI // Store the current pI value
        pI = equationParams._2
      }
      // Change pH value using the delta parameter for first loop
      else {
        previousPI = pI // Store the current pH value
        // Modify pI for next round
        if (charge > 0) pI += delta else pI -= delta
        delta /= 2
      }

      // Store the current charge value
      previousCharge = charge

      iterCount += 1
    }

    pI.toFloat
  }
  
  /**
   * Computes protein charge at corresponding pH.
   * See: http://pepcalc.com/notes.php?nc
   */
  private def _computeProteinCharge(aaAbundanceMap: HashMap[String,Float], pH: Double): Double = {
  
    var charge = 0.0
    
    for( ( pkSymbol, pK ) <- pkTable; aaCount <- aaAbundanceMap.get(pkSymbol) ) {
      if (fullAcidSet.contains(pkSymbol)) charge -= aaCount * _computePartialCharge(pH,pK)
      if (fullBaseSet.contains(pkSymbol)) charge += aaCount * _computePartialCharge(pK,pH)
    }
    
    charge
  }
  
  private def _computePartialCharge(val1: Double, val2: Double): Double = {
    // Compute concentration ratio
    val cRatio = 10**(val1-val2)
    
    // Compute and return partial charge
    cRatio / (cRatio+1)
  }
  
}

