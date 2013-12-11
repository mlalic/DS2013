package common.metadata;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;

public class Md5Hasher implements KeyHasher {
	private final static String DEFAULT_CHARSET = "ASCII";
	
	private String minHash;
	private String maxHash;
	
	public Md5Hasher() {
		// Builds the min/max hash values on instantiation
		minHash = "";
		maxHash = "";
		for (int i = 0; i < 32; ++i) {
			minHash += "0";
			maxHash += "F";
		}
	}

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

	@Override
	public String getMaxHash() {
		return maxHash;
	}

	@Override
	public String getMinHash() {
		return minHash;
	}

}
