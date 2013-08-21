package fr.proline.util.version;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class VersionHelper {

    private static final Logger LOG = LoggerFactory.getLogger(VersionHelper.class);

    /* Private constructor (Utility class) */
    private VersionHelper() {
    }

    /**
     * Retreives all services implementing <code>IVersion</code> interface via <code>ServiceLoader</code>.
     * 
     * @return Array of found services (can be empty).
     */
    public static IVersion[] getVersions() {
	final ServiceLoader<IVersion> versionLoader = ServiceLoader.load(IVersion.class);

	Iterator<IVersion> iter = versionLoader.iterator(); // Lazy iterator

	final List<IVersion> versions = new ArrayList<IVersion>();

	/* Protect hasNext() and next() methods from ServiceConfigurationError */
	boolean hasNext = false;

	do {
	    hasNext = false; // Pessimistic initialization

	    try {
		hasNext = iter.hasNext();

		if (hasNext) {
		    final IVersion v = iter.next();
		    versions.add(v);
		}

	    } catch (ServiceConfigurationError sce) {
		LOG.error("Error retrieving IVersion service", sce);
	    }

	} while (hasNext);

	return versions.toArray(new IVersion[0]);
    }

}
