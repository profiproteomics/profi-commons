package fr.proline.util;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Properties;

import org.junit.Test;

public class PropertiesUtilsTest {

    private static final String TEST_RESOURCES_PATH = "src" + File.separatorChar + "test"
	    + File.separatorChar + "resources" + File.separatorChar;

    private static final String PROPERTIES_FILE_NAME = "test.properties";

    @Test
    public void test() {
	final Properties propsClassLoader = PropertiesUtils.loadProperties(PROPERTIES_FILE_NAME);
	assertTrue("Properties file loaded by ClassLoader",
		(propsClassLoader != null) && !propsClassLoader.isEmpty());

	final String totoValue = PropertiesUtils.getProperty(propsClassLoader, "toto");
	assertEquals("Property value from ClassLoader", "1", totoValue);

	final Properties propsFile = PropertiesUtils.loadProperties(TEST_RESOURCES_PATH
		+ PROPERTIES_FILE_NAME);
	assertTrue("Properties file loaded as regular file", (propsFile != null) && !propsFile.isEmpty());

	final String tataValue = PropertiesUtils.getProperty(propsFile, "tata");
	assertEquals("Property value as regular file", "yes", tataValue);
    }

}
