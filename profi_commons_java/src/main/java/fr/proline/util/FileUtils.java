package fr.proline.util;

public final class FileUtils {

    /* Private constructor (Utility class) */
    private FileUtils() {
    }

    public static String[] splitFilePath(final String abstractPathname) {

	if (abstractPathname == null) {
	    throw new IllegalArgumentException("AbstractPathname is null");
	}

	final boolean isLinux = abstractPathname.contains("/");
	final boolean isWindows = abstractPathname.contains("\\");

	if (isLinux && isWindows) {
	    throw new IllegalArgumentException("Cannot guess Linux or Windows file name separator from ["
		    + abstractPathname + ']');
	}

	String[] result = null;

	if (isLinux) {
	    result = abstractPathname.split("/+");
	} else if (isWindows) {
	    result = abstractPathname.split("\\\\+");
	} else {
	    result = new String[] { abstractPathname, };
	}

	return result;
    }

    public static String extractFileName(final String abstractPathname) {
	final String[] pathParts = FileUtils.splitFilePath(abstractPathname);

	String fileName = null;

	final int nParts = pathParts.length;

	if (nParts > 0) {
	    fileName = pathParts[nParts - 1];
	} else {
	    throw new IllegalArgumentException("Invalid abstractPathname [" + abstractPathname + ']');
	}

	return fileName;
    }

}
