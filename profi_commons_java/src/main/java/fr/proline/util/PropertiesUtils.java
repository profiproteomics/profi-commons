package fr.proline.util;

import static fr.proline.util.StringUtils.LINE_SEPARATOR;
import static fr.proline.util.StringUtils.NULL;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for handling <code>Properties</code> and properties files.
 * 
 * @author LMN
 * 
 */
public final class PropertiesUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesUtils.class);

    private static final int BUFFER_LENGTH = 2048;

    /* Private constructor (Utility class) */
    private PropertiesUtils() {
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
     * Tries to retrive a property value from a non-typed properties Map.
     * 
     * @param properties
     *            Non-typed properties Map (must not be <code>null</code>).
     * @param key
     *            Key of the property (must not be <code>null</code> : <code>Properties</code> is a
     *            <code>Hashtable</code>).
     * @return Property value as String or <code>null</code> if not defined or the value is not a
     *         <code>String</code> instance.
     */
    public static String getProperty(final Map<Object, Object> properties, final String key) {

	if (properties == null) {
	    throw new IllegalArgumentException("Properties Map is null");
	}

	if (key == null) {
	    throw new IllegalArgumentException("Key is null");
	}

	String result = null;

	final Object value = properties.get(key);

	if (value instanceof String) {
	    result = (String) value;
	} else if (value != null) {
	    LOG.warn(String.format("Properties Map contains non String value for %s : %s [%s]", key, value
		    .getClass().getName(), value));
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
    public static String formatProperties(final Map<Object, Object> props) {

	if (props == null) {
	    throw new IllegalArgumentException("Props Map is null");
	}

	final StringBuilder buff = new StringBuilder(BUFFER_LENGTH);

	synchronized (props) {

	    for (final Map.Entry<Object, Object> entry : props.entrySet()) {
		final Object key = entry.getKey();

		if (key == null) {
		    buff.append(NULL);
		} else {
		    buff.append(key);
		}

		buff.append(" : ");

		final Object value = entry.getValue();

		if (value == null) {
		    buff.append(NULL);
		} else {
		    buff.append(value.getClass().getName());
		    buff.append(" [").append(value).append(']');
		}

		buff.append(LINE_SEPARATOR);
	    }

	} // End of synchronized block on props

	return buff.toString();
    }

}
