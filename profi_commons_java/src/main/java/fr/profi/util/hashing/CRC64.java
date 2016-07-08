package fr.profi.util.hashing;

/**
 * CRC64 checksum calculator based on the polynom specified in the ISO 3309 standard. 
 * The generator polynomial function is x64 + x4 + x3 + x + 1.
 * Note that Uniprot uses exactly the same algorithm internally.
 * 
 * This implementation is based on the following publications:
 * 
 * <ul>
 * <li>W. H. Press, S. A. Teukolsky, W. T. Vetterling, and B. P. Flannery,
 * "Numerical recipes in C", 2nd ed., Cambridge University Press. Pages 896ff.</li>
 * <li>http://en.wikipedia.org/wiki/Cyclic_redundancy_check</li>
 * <li>http://www.ross.net/crc/crcpaper.html</li>
 * </ul>
 * 
 * For a Python implementation see: http://code.activestate.com/recipes/259177/
 * 
 */
public final class CRC64 {

	private static final long POLY64REV = 0xd800000000000000L;

	private static final long[] LOOKUPTABLE;

	static {
		LOOKUPTABLE = new long[0x100];
		for (int i = 0; i < 0x100; i++) {
			long v = i;
			for (int j = 0; j < 8; j++) {
				if ((v & 1) == 1) {
					v = (v >>> 1) ^ POLY64REV;
				} else {
					v = (v >>> 1);
				}
			}
			LOOKUPTABLE[i] = v;
		}
	}

	/**
	 * Calculates the CRC64 checksum for the given data array.
	 * 
	 * @param data
	 *            data to calculate checksum for
	 * @return checksum value
	 */
	public static long checksum(final byte[] data) {
		long sum = 0;
		for (final byte b : data) {
			final int lookupidx = ((int) sum ^ b) & 0xff;
			sum = (sum >>> 8) ^ LOOKUPTABLE[lookupidx];
		}
		return sum;
	}

	private CRC64() {
	}

}