package fr.profi.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to handle character strings.
 * 
 * @author Laurent Martin
 * 
 *         Created 31 Jul. 2012
 * 
 * 
 * 
 */
public final class StringUtils {

	/* Constants */
	private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

	/**
	 * Platform-specific line separator (Windows CRLF, UNIX LF).
	 */
	public static final String LINE_SEPARATOR = getLineSeparator();

	public static final String NULL = "NULL";

	private static final String LINE_SEPARATOR_KEY = "line.separator";

	/* Private constructor (Utility class) */
	private StringUtils() {
	}

	/* Public class methods */
	/**
	 * Checks if given String is empty (<code>null</code>, "", or contains only white-space characters).
	 * 
	 * @param s
	 *            Source String to check
	 * @return <code>true</code> if given String is <code>null</code> or empty
	 */
	public static boolean isEmpty(final String s) {
		boolean empty = true; // Optimistic initialization

		if (s != null) {

			final int stringLength = s.length();
			for (int index = 0; empty && (index < stringLength); ++index) {
				empty = Character.isWhitespace(s.charAt(index));
			}

		}

		return empty;
	}

	/**
	 * Checks if given String is not empty (same rules than the <code>isEmpty</code> method).
	 * 
	 * @param s
	 *            Source String to check
	 * @return <code>true</code> if given String is not <code>null</code> and not empty
	 */
	public static boolean isNotEmpty(final String s) {
		return !StringUtils.isEmpty(s);
	}

	/**
	 * Check if a given CharSequence (String or buffer) is terminated by a given character.
	 * 
	 * @param cs
	 *            Souce CharSequence to check
	 * @param term
	 *            Terminator char
	 * 
	 * @return <code>true</code> if last character of given CharSequence is <em>term</em>
	 */
	public static boolean isTerminated(final CharSequence cs, final char term) {
		boolean terminated = false;

		if (cs != null) {
			final int length = cs.length();
			terminated = ((length > 0) && (cs.charAt(length - 1) == term));
		}

		return terminated;
	}

	/**
	 * Converts a char residue to a string value.
	 * 
	 * @param residue
	 *            Residue as char primitive.
	 * @return Residue as a string or <code>null</code> if <code>residue == '\0'</code>.
	 */
	public static String convertCharResidueToString(final char residue) {
		String result = null;

		if (residue != '\0') {
			result = Character.toString(residue);
		}

		return result;
	}

	/**
	 * Converts a string residue to a char primitive.
	 * 
	 * @param strResidue
	 *            Residue as string object.
	 * @return Residue as first char of <code>strResidue</code> or <code>'\0'</code> if <code>strResidue</code> is <code>null</code> or emty.
	 */
	public static char convertStringResidueToChar(final String strResidue) {
		char result = '\0';

		if (!isEmpty(strResidue)) {
			result = strResidue.charAt(0);
		}

		return result;
	}

	/* Private methods */
	private static String getLineSeparator() {
		String result = null;

		// Try to retrieve line separator String from System properties
		try {
			result = System.getProperty(LINE_SEPARATOR_KEY);
		} catch (Exception ex) {
			LOG.error("Unable to retrieve System property \"" + LINE_SEPARATOR_KEY + '\"', ex);
		}

		if (result == null) {
			// Default : use UNIX newline
			result = "\n";
		}

		return result;
	}

}
