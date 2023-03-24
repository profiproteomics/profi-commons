package fr.profi.util;

import org.apache.commons.math3.linear.*;


public final class MathUtils {

	/**
	 * For <code>float</code> computations.
	 */
	public static final float EPSILON_FLOAT = 1e-6f;

	/**
	 * For <code>double</code> with <em>float precision</em> computations.
	 */
	public static final double EPSILON_LOW_PRECISION = 1e-6;

	/**
	 * For <code>double</code> with <em>double precision</em> computations.
	 */
	public static final double EPSILON_HIGH_PRECISION = 1e-14;
	
	public static boolean nearlyEquals(double a, double b, double eps) {
		if (a==b) return true;
		return Math.abs(a - b) < eps;
	}

	private MathUtils() {
	}

	public static double[][] matrixSolver(double[][] coefficients, double[][] values, boolean coeffTransposed ){
		if(values == null || values.length ==0)
			return new double[0][0];

		double[][] tCoefficients = coeffTransposed ? coefficients : transposeMatrix(coefficients);
		double[][] newValues = new double[values.length][values[0].length];
		RealMatrix coeffMatrix = new Array2DRowRealMatrix(tCoefficients,false );
		DecompositionSolver solver = new LUDecomposition(coeffMatrix).getSolver();
		RealVector constants = null;
		RealVector result = null;
		for (int i =0; i<values.length; i++){
			double[] rowVal = values[i];
			constants = new ArrayRealVector(rowVal, false);
			result = solver.solve(constants);
			newValues[i] = result.toArray();
		}
		return newValues;
	}

	public static double[][] transposeMatrix(double [][] m){
		int d1 = m.length;
		int d2 = m[0].length;
		double[][] temp = new double[d2][d1];
		for (int i = 0; i < d1; i++)
			for (int j = 0; j < d2; j++)
				temp[j][i] = m[i][j];
		return temp;
	}
}
