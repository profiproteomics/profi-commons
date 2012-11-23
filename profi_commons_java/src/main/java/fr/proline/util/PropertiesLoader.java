package fr.proline.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);

    public static Properties loadProperties(final String name) {

	if (StringUtils.isEmpty(name)) {
	    throw new IllegalArgumentException("Invalid properties name");
	}

	Properties result = null;

	InputStream is = null;

	try {
	    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

	    is = classLoader.getResourceAsStream(name);

	    if (is == null) {
		is = new FileInputStream(name);

		LOG.debug("Loding [{}] as file", name);
	    } else {
		LOG.debug("Loading [{}] as resource from ClassLoader");
	    }

	    final Properties props = new Properties();

	    props.load(is);

	    result = props;
	} catch (Exception ex) {
	    LOG.error("Error loading [" + name + ']', ex);
	} finally {

	    if (is != null) {
		try {
		    is.close();
		} catch (Exception exClose) {
		    LOG.error("Error closing [" + name + ']', exClose);
		}
	    }

	}

	return result;
    }

}
