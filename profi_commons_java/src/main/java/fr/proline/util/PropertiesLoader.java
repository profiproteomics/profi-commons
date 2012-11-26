package fr.proline.util;

import static fr.proline.util.StringUtils.LINE_SEPARATOR;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for loading a properties file from <code>ClassLoader</code> or regular file.
 * 
 * @author LMN
 * 
 */
public final class PropertiesLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);

    private static final int BUFFER_LENGTH = 2048;

    /* Private constructor (Utility class) */
    private PropertiesLoader() {
    }

    /* Public class methods */
    /**
     * Tries to load a properties file into a <code>Properties</code> object.
     * <p>
     * Warning : Properties file are loaded via <code>InputStream</code> which assumed to use the ISO 8859-1
     * character encoding.
     * 
     * @param name
     *            Name of the properties file (must not be <code>null</code> or empty). This method first
     *            search the properties file as a resource via current <code>ClassLoader</code>. If the
     *            properties file is not found in class path, this method tries to load it as a regular file
     *            (absolute or relative to current directory).
     * @return The loaded <code>Properties</code> instance or <code>null</code> if the file is not found or an
     *         I/O error occurs.
     */
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

		LOG.debug("Loading [{}] as file", name);
	    } else {
		LOG.debug("Loading [{}] as resource from ClassLoader", name);
	    }

	    final Properties props = new Properties();

	    props.load(is);

	    if (LOG.isDebugEnabled()) {
		LOG.debug("Properties [" + name + "] :" + LINE_SEPARATOR + formatProperties(props));
	    }

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

    /**
     * Formats properties for debug logging purpose.
     * 
     * @param props
     *            Properties (must not be <code>null</code>).
     * @return Formatted string containing all key - value pairs.
     */
    public static String formatProperties(final Properties props) {

	if (props == null) {
	    throw new IllegalArgumentException("Props is null");
	}

	final StringBuilder buff = new StringBuilder(BUFFER_LENGTH);

	synchronized (props) {

	    for (final Map.Entry<Object, Object> entry : props.entrySet()) {
		buff.append(entry.getKey()).append(" [").append(entry.getValue()).append(']');
		buff.append(LINE_SEPARATOR);
	    }

	}

	return buff.toString();
    }

}
