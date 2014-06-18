package fr.proline.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testIsEmpty() {
		String str = (String) null;
		assertTrue("Chaine null", StringUtils.isEmpty(str));

		str = "";
		assertTrue("Chaine vide", StringUtils.isEmpty(str));

		str = " ";
		assertTrue("Un espace", StringUtils.isEmpty(str));

		str = "\t";
		assertTrue("Une tabulation", StringUtils.isEmpty(str));

		str = "\t\t";
		assertTrue("Deux tabulations", StringUtils.isEmpty(str));

		str = "  ";
		assertTrue("Deux espaces", StringUtils.isEmpty(str));

		str = " \t";
		assertTrue("Espace + tab", StringUtils.isEmpty(str));

		str = "a";
		assertFalse("Un caractère", StringUtils.isEmpty(str));

		str = "aa";
		assertFalse("Deux caractères", StringUtils.isEmpty(str));

		str = " a\t";
		assertFalse("Espace, un caractère, une tabulation", StringUtils.isEmpty(str));

		str = "  toto\t";
		assertFalse("Deux espaces, chaine non vide, une tabulation", StringUtils.isEmpty(str));
	}

	@Test
	public void testResidue() {
		final char expectedChar = 'A';

		final String expectedStr = "A";

		final String strResidue = StringUtils.convertCharResidueToString(expectedChar);

		assertEquals("Char -> String", expectedStr, strResidue);

		final char charResidue = StringUtils.convertStringResidueToChar(strResidue);

		assertEquals("String -> char", expectedChar, charResidue);
	}

}
