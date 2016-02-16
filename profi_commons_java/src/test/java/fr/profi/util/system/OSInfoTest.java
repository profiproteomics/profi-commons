package fr.profi.util.system;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OSInfoTest {

	private static final Logger LOG = LoggerFactory.getLogger(OSInfoTest.class);

	@Test
	public void testGetOSType() {
		final OSType osType = OSInfo.getOSType();

		assertNotNull("Returned OS Type", osType);

		LOG.info("Detected OS Type : {}", osType);
	}

}
