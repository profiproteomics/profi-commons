package fr.profi.util;

public class BytesUtils {

	private static char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	static String bytes2HexString(byte[] bytes) {
		return new String(bytes2HexChars(bytes));
	}

	static char[] bytes2HexChars(byte[] bytes) {

		char[] chars = new char[2 * bytes.length];
		int i = 0;
		while (i < bytes.length) {
			byte b = bytes[i];
			chars[2 * i] = HEX_CHARS[(b & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[b & 0x0F];
			i++;
		}

		return chars;
	}
}
