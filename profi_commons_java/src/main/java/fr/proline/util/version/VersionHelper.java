package fr.proline.util.version;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

public final class VersionHelper {

    /* Private constructor (Utility class) */
    private VersionHelper() {
    }

    /**
     * Retrives all services implementing <code>IVersion</code> interface via <code>ServiceLoader</code>.
     * 
     * @return Array of found services (can be empty).
     */
    public static IVersion[] getVersions() {
	final ServiceLoader<IVersion> versionLoader = ServiceLoader.load(IVersion.class);

	final List<IVersion> versions = new ArrayList<IVersion>();

	for (final IVersion v : versionLoader) {
	    versions.add(v);
	}

	return versions.toArray(new IVersion[0]);
    }

}
