package fr.profi.util.security;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SecurityUtilsTest {

	@Test
	public void testHashString() {
		final String textToHash = "MyPasswordTest";
		assertEquals("SHA256 Password", SecurityUtils.sha256Hex(textToHash), "2ce97f4c26308c59467c19575595ab8728ec5bf6fd54f2ceb632dfb83ffb64a9");
	}

}
