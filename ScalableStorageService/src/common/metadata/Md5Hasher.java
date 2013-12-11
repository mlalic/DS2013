package common.metadata;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class Md5Hasher implements KeyHasher {
	private final static String DEFAULT_CHARSET = "ASCII";
	
	@Override
	public String getKeyHash(String key) {
		MessageDigest hasher;
		try {
			hasher = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		return Hex.encodeHexString(hasher.digest(keyToBytes(key)));
	}
	
	private byte[] keyToBytes(String key) {
		try {
			return key.getBytes(DEFAULT_CHARSET);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

}
