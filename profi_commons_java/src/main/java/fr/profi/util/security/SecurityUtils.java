package fr.profi.util.security;

// TODO: remove this file and use the HashingUtils.java class instead
import fr.profi.util.HashingUtils;

public class SecurityUtils {

	public static String sha256Hex(String text) {
		return HashingUtils.sha256Hex(text);
	}

}
