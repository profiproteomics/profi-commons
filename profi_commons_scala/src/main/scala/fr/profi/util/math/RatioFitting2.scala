package fr.profi.util.math

import com.typesafe.scalalogging.LazyLogging
import fr.profi.util.primitives.isZeroOrNaN
import org.apache.commons.math3.linear.{Array2DRowRealMatrix, ArrayRealVector, SingularValueDecomposition}

import scala.collection.mutable.ArrayBuffer


object RatioFitting2 extends LazyLogging {

  def fit(abundanceMatrix: Array[Array[Float]], minAbundances: Array[Float]): Array[Float] = {
    fitWithoutImputation(abundanceMatrix)
    // val logMinAbundances = minAbundances.map { Math.log(_)/Math.log(2) }
    //_fit(logAbundances, buildImputedRatioMatrix(logAbundances, logMinAbundances, valuesCountByColumns), valuesCountByColumns)
  }

  def fitWithoutImputation(abundanceMatrix: Array[Array[Float]]): Array[Float] = {
    val valuesCountByColumns = abundanceMatrix.transpose.map{col => col.count(!_.isNaN)}
    val logAbundances = abundanceMatrix.map{_.map{ Math.log(_).toFloat/Math.log(2).toFloat }}
    val fit = _fit(logAbundances, buildRatioMatrix(logAbundances), valuesCountByColumns)
    if (fit.isDefined) {
      _rescaleCoefficients(fit.get, abundanceMatrix, valuesCountByColumns)
    } else {
      abundanceMatrix.transpose.map{ _calcAbundanceSum( _ ) }
    }
  }

  def fitWithCountPredicate(abundanceMatrix: Array[Array[Float]], minRatioCount: Int): Array[Float] = {
    val logAbundances = abundanceMatrix.map{_.map{ Math.log(_).toFloat/Math.log(2).toFloat }}
    var logMatrix = logAbundances.transpose
    var valuesCountByColumns = logMatrix.map{col => col.count(!_.isNaN)}
    // do not take into account columns with to few count of values
    valuesCountByColumns = valuesCountByColumns.map(c => if (c < math.min(minRatioCount, abundanceMatrix.length-1)) 0 else c )
    // set to NaN conditions (=columns) without enough values to avoid ratio computation for this columns
    valuesCountByColumns.zipWithIndex.foreach{ case (c, index) => 
      if (c == 0) {
        logMatrix(index) = Array.fill[Float](abundanceMatrix.length)(Float.NaN)
      }
    }
    logMatrix = logMatrix.transpose
    val fit = _fit(logMatrix, buildRatioMatrix(logMatrix), valuesCountByColumns)
    if (fit.isDefined) {
      _rescaleCoefficients(fit.get, abundanceMatrix, valuesCountByColumns)
    } else {
      abundanceMatrix.transpose.map{ _calcAbundanceSum( _ ) }
    }
  }


  private def _fit(abundanceMatrix: Array[Array[Float]], logRatioMatrix: Array[Array[Double]], valuesCountByColumns: Array[Int]): Option[Array[Double]] = {

    val opt = buildMatrices(abundanceMatrix) // buildMatrices_V1(abundanceMatrix, logRatioMatrix)

    if (opt.isDefined) {
      val (matrixA, filteredLogRatios) = opt.get

      val solver = new SingularValueDecomposition(new Array2DRowRealMatrix(matrixA, false)).getSolver()
      val matrixB = new ArrayRealVector(filteredLogRatios, false)
      val solution = solver.solve(matrixB);

      val solutionMatrix = solution.toArray()

      Some(solutionMatrix)
    } else {
      None
    }
  }

  private def _rescaleCoefficients(initialCoeffs: Array[Double], abundanceMatrix: Array[Array[Float]], valuesCountByColumns: Array[Int]): Array[Float] = {

    var coeffs = initialCoeffs.zipWithIndex.map{ case (p, i) => if (valuesCountByColumns(i) == 0)  { 0.0 } else { Math.pow(2,p) } }
    val coeffSum = coeffs.foldLeft(0.0)(_ + _)
    val intensitySum = abundanceMatrix.map(_.foldLeft(0.0)((a, b) => if (b.isNaN()) { a } else { a + b })).foldLeft(0.0)((a, b) => a + b)
    coeffs = coeffs.map(_ * intensitySum / coeffSum);

    var abundances = coeffs.map(_.toFloat)
    abundances = abundances.map { p => if (p == 0.0) { Float.NaN } else { p } };
    abundances
  }

  private def buildMatrices_V1(abundanceMatrix: Array[Array[Float]], logRatioMatrix: Array[Array[Double]]) : Option[(Array[Array[Double]], Array[Double])] = {
    var logRatios = columnMedians(logRatioMatrix)
    val naNRatiosIndexes = logRatios.zipWithIndex.filter(p => p._1.isNaN).map(_._2)
    logRatios = logRatios.filter { !_.isNaN() }
    if (logRatios.isEmpty) {
      logger.warn("No eligible columns for ratios computation")
      return None
    }
    val matrixA = buildCoefficientMatrix(abundanceMatrix(0).length, naNRatiosIndexes)
    Some((matrixA, logRatios))
  }

  private def buildRatioMatrix(logAbundances: Array[Array[Float]]): Array[Array[Double]] = {
    // Compute the number of combination of 2 items among logAbundances(0).length : n!/(n-p)!p! with p = 2
    // previous formula was CombinatoricsUtils.factorial(logAbundances(0).length) / (2 * CombinatoricsUtils.factorial(logAbundances(0).length - 2))
    val n = logAbundances(0).length
    val length = n*(n-1)/2
    val matrix = ArrayBuffer[Array[Double]]()

    for (row <- logAbundances) {
      val nr = Array.fill[Double](length)(Double.NaN)
      var index = 0
      for (i <- 0 to (row.length - 2)) {
        for (j <- (i + 1) to (row.length - 1)) {
          if (!row(i).isNaN() && !row(j).isNaN() && (row(i) * row(j)) != 0) { nr(index) = row(j) - row(i) }
          index = index + 1
        }
      }
      matrix += nr
    }
    (matrix.toArray)
  }

  def buildCoefficientMatrix(numberOfSamples: Int, naNRatiosIndexes: Array[Int]): Array[Array[Double]] = {

    val matrix = ArrayBuffer[Array[Double]]()
    var row = 0
    for (i <- 0 to (numberOfSamples - 2)) {
      for (j <- (i + 1) to (numberOfSamples - 1)) {
        if (!naNRatiosIndexes.contains(row)) {
          val nr = Array.fill[Double](numberOfSamples)(0)
          nr(i) = -1.0
          nr(j) = 1.0
          matrix += nr
        }
        row = row + 1
      }
    }
    matrix.toArray
  }


  private def buildMatrices(logAbundances: Array[Array[Float]]) : Option[(Array[Array[Double]], Array[Double])] = {

    val numberOfSamples = logAbundances(0).length
    val matrixA = Array.fill(numberOfSamples, numberOfSamples)(0.0)
    val matrixB = Array.fill(numberOfSamples)(0.0)

    for (i <- 0 to (numberOfSamples - 2)) {
      for (j <- (i + 1) to (numberOfSamples - 1)) {
        val medianRatio = columnsMedianRatio(logAbundances, i, j)
        if (!medianRatio.isNaN) {
          matrixA(i)(j) = -1.0
          matrixA(j)(i) = -1.0
          matrixA(i)(i) = matrixA(i)(i) + 1.0
          matrixA(j)(j) = matrixA(j)(j) + 1.0
          matrixB(j) = matrixB(j) + medianRatio
          matrixB(i) = matrixB(i) - medianRatio
        }
      }
    }

    Some((matrixA, matrixB))
  }

  private def columnsMedianRatio(values: Array[Array[Float]], i: Int, j: Int) : Double = {
    val ci = values.map{ _.apply(i) }
    val cj = values.map{ _.apply(j) }
    val medians = ci.zip(cj).map{ case (a, b) =>  if (a.isNaN | b.isNaN) { Double.NaN } else { b - a } }
    filteredMedian(medians)
  }

  private def columnMedians(values: Array[Array[Double]]): Array[Double] = {
    val medians = Array.fill[Double](values(0).length)(0)
    for (col <- 0 to values(0).length - 1) {
      val colValues = Array.fill[Double](values.length)(Double.NaN)
      var k = 0
      for (row <- values) {
        colValues(k) = row(col)
        k = k + 1
      }
      medians(col) = filteredMedian(colValues)
    }
    medians
  }

  private def _calcAbundanceSum(abundances: Array[Float]): Float = {
    val defAbundances = abundances.filter( isZeroOrNaN(_) == false )
    if( defAbundances.length == 0 ) Float.NaN else defAbundances.sum
  }
}