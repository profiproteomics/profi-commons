package fr.profi.util;

import static fr.profi.util.MathUtils.EPSILON_FLOAT;
import static fr.profi.util.MathUtils.EPSILON_HIGH_PRECISION;
import static org.junit.Assert.*;

import org.junit.Test;

public class MathUtilsTest {

	@Test
	public void testEpsilons() {
		final float floatValue = (1.0f / 3) * 3;
		assertEquals("Float epsilon", 1.0f, floatValue, EPSILON_FLOAT);

		final double doubleValue = (1.0 / 3) * 3;
		assertEquals("Double epsilon", 1.0, doubleValue, EPSILON_HIGH_PRECISION);
	}

}
