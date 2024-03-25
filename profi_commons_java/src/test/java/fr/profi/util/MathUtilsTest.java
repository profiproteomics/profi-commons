package fr.profi.util;

import static fr.profi.util.MathUtils.*;
import static org.junit.Assert.*;

import org.apache.commons.math3.util.Precision;
import org.junit.Test;

public class MathUtilsTest {

	static double[][] coefficient = { {00.929,0.059,0.002,0.000},
			{ 0.020,0.923,0.056,0.001 },
			{ 0.000,0.030,0.924,0.045},
			{ 0.000,0.001,0.040,0.923}};

	static  double[][] valuesToOne = {{929 , 59,  2, 0},
			{  20,923,56,1 },
			{0,30,924,45 },
			{0,1,40,923 }};

	static  double[][] expectedCorrectedValuesToOne = {{1000 , 0,  0, 0},
			{  0,1000,0,0 },
			{0,0,1000,0 },
			{0,0,0,1000 }};
	static  double[][] values = {{1347.6158 , 2247.3097,  3927.6931, 7661.1463},
			{  739.9861,   799.3501 ,   712.5983,   940.6793 },
			{27638.3582, 33394.0252 , 32104.2879, 26628.7278 },
			{31892.8928, 33634.6980 , 37674.7272, 37227.7119 },
			{26143.7542, 29677.4781 , 29089.0593, 27902.5608 },
			{ 6448.0829,  6234.1957 ,  6902.8903,  6437.2303 }};


	static  double[][] expectedCorrectedValues = {
			{ 1402.9441 ,  2214.0346,  3762.2549,  8114.4429 },
			{  779.4666 ,   793.0792,   678.8084,   985.2003 },
			{29034.3781 , 33271.0470, 31484.7132, 27279.1383 },
			{33618.9092 , 33046.3075, 37031.6133, 38492.1376 },
			{27508.0039 , 29440.9295, 28390.4560, 28814.2463 },
			{ 6809.7601 ,  6090.7894,  6799.5030,  6636.1450 }};

	@Test
	public void testEpsilons() {
		final float floatValue = (1.0f / 3) * 3;
		assertEquals("Float epsilon", 1.0f, floatValue, EPSILON_FLOAT);

		final double doubleValue = (1.0 / 3) * 3;
		assertEquals("Double epsilon", 1.0, doubleValue, EPSILON_HIGH_PRECISION);
	}

	@Test
	public void testSolveMatrix() {
		double[][] retValues = MathUtils.matrixSolver(coefficient, values, false);
		assertEquals(expectedCorrectedValues.length, retValues.length);

		for (int i=0; i<retValues.length; i++){
			double[] rowRetVal = retValues[i];
			double[] rowExpectedVal = expectedCorrectedValues[i];
			for (int j = 0; j<rowExpectedVal.length; j++) {
				assertEquals(rowExpectedVal[j], Precision.round(Double.valueOf(rowRetVal[j]),4 ), EPSILON_LOW_PRECISION);
			}
		}

	 	retValues = MathUtils.matrixSolver(coefficient, valuesToOne,false);
		assertEquals(4, retValues.length);

		for (int i=0; i<retValues.length; i++){
			double[] rowRetVal = retValues[i];
			double[] rowExpectedVal = expectedCorrectedValuesToOne[i];
			for (int j = 0; j<rowRetVal.length; j++) {
				System.out.println(" Ret Val ! "+i+"/"+j+" : "+rowRetVal[j]);
				assertEquals(rowExpectedVal[j], Precision.round(Double.valueOf(rowRetVal[j]),4 ), EPSILON_LOW_PRECISION);
			}
		}
	}

}
