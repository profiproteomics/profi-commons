package fr.profi.util.security;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.profi.util.DateUtils;

/**
 * Used to encrypt of decrypt strings
 * 
 * Encryption usage : // prepare public key by Actor1 EncryptionManager encryptionManager = EncryptionManager.getEncryptionManager(); String publicKey =
 * encryptionManager.getPublicKeyAsString(); // publicKey is given to Actor2
 * 
 * // ecryption by Actor2 thanks to the publicKey String ecryptedString = EncryptionManager.encrypt("test", publicKey);
 * 
 * //decryption by Actor1 String result = encryptionManager.decrypt(ecryptedString);
 * 
 * @author JM235353
 */
public class EncryptionManager {

	private static EncryptionManager m_singleton = null;
	private static final Logger LOG = LoggerFactory.getLogger(DateUtils.class);

	private KeyPair m_keypair = null;

	public static EncryptionManager getEncryptionManager() {
		if (m_singleton == null) {
			m_singleton = new EncryptionManager();
		}

		return m_singleton;
	}

	private EncryptionManager() {
		generateKeys();
	}

	private void generateKeys() {
		try {
			KeyPairGenerator keygenerator = KeyPairGenerator.getInstance("RSA");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keygenerator.initialize(1024, random);
			m_keypair = keygenerator.generateKeyPair();
		} catch (NoSuchAlgorithmException | NoSuchProviderException e) {
			LOG.error("Specified algorithm doesn't exist !", e);
			throw new IllegalArgumentException("Specified algorithm doesn't exist !"); // should not happen
		}
	}

	public String getPublicKeyAsString() {

		PublicKey publicKey = m_keypair.getPublic();
		byte[] publicKeyEncoded = publicKey.getEncoded();
		String publicKeyAsString = Base64.encodeBase64String(publicKeyEncoded);

		return publicKeyAsString;
	}

	public String decrypt(String encryptedString) {

		try {
			byte[] encrypted = Base64.decodeBase64(encryptedString);

			PrivateKey privateKey = m_keypair.getPrivate();
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decrypted = cipher.doFinal(encrypted);

			String text = new String(decrypted, "UTF-8");

			return text;
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
			| UnsupportedEncodingException e) {
			// should not happen
			LOG.error("Error while decrypting specified text", e);
		}
		return null;

	}

	public static String encrypt(String text, String publicKeyAsString) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, retrievePublicKey(publicKeyAsString));
			byte[] encrypted = cipher.doFinal(text.getBytes("UTF-8"));
			return Base64.encodeBase64String(encrypted);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException
			| UnsupportedEncodingException e) {
			// should not happen
			LOG.error("Error while crypting specified text ", e);
		}
		return null;
	}

	private static PublicKey retrievePublicKey(String publicKeyAsString) {

		try {
			byte[] publicKeyEncoded = Base64.decodeBase64(publicKeyAsString);
			X509EncodedKeySpec ks = new X509EncodedKeySpec(publicKeyEncoded);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			PublicKey publicKey = kf.generatePublic(ks);
			return publicKey;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			LOG.error("Specified algorithm doesn't exist !", e);
		}
		return null;
	}

}
