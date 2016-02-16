package fr.profi.util.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtils {

	public static String sha256Hex(String text) {
		byte[] encodedText = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes("UTF-8"));
			encodedText = md.digest();

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return null;
		}
		return bytes2HexString(encodedText);
	}

	private static char[] HEX_CHARS = "0123456789abcdef".toCharArray();

	private static String bytes2HexString(byte[] bytesValue) {

		char[] chars = new char[2 * bytesValue.length];
		int i = 0;
		while (i < bytesValue.length) {
			byte b = bytesValue[i];
			chars[2 * i] = HEX_CHARS[(b & 0xF0) >>> 4];
			chars[2 * i + 1] = HEX_CHARS[b & 0x0F];
			i++;
		}
		return new String(chars);
	}
}
