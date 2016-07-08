package fr.profi.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import fr.profi.util.hashing.CRC64;

public class HashingUtils {

	public static String sha256Hex(String text) {
		byte[] encodedText = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(text.getBytes("UTF-8"));
			encodedText = md.digest();

		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			return null;
		}
		
		return BytesUtils.bytes2HexString(encodedText);
	}
	
	public static String crc64Hex(String text) {
		
		long crcValue = CRC64.checksum(text.getBytes());
		
		return String.format("%016X", crcValue);
	}

}
