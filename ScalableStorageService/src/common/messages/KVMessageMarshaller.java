package common.messages;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class KVMessageMarshaller {
	public static final int MAX_KEY_SIZE = 20; // bytes
	public static final int MAX_VALUE_SIZE = 120 * 1024; // bytes
	
	private static final byte[] TERMINATOR = new byte[] { 0 };
	private static final String CHARSET_NAME = "ASCII";
	
	
	public byte[] marshal(KVMessage message) throws Exception {
		byte messageType = (byte) (0xFF & message.getStatus().ordinal());

		// Preconditions for the key/value sizes asserted
		String key = message.getKey();
		if (key != null && key.length() > MAX_KEY_SIZE) {
			throw new Exception("The key is too long");
		}
		String value = message.getValue();
		if (value != null && value.length() > MAX_VALUE_SIZE) {
			throw new Exception("The value is too long");
		}
		
		// Convert the key to a byte representation according to the protocol
		byte[] serializedKey = null;
		try {
			serializedKey = serializeString(key);
		} catch (UnsupportedEncodingException exc) {
			throw new Exception("The key cannot be encoded as " + CHARSET_NAME);
		}
		
		// Convert the value to a byte representation according to the protocol
		byte[] serializedValue = null;
		try {
			serializedValue = serializeString(value);
		} catch (UnsupportedEncodingException exc) {
			throw new Exception("The value cannot be encoded as " + CHARSET_NAME);
		}
		
		byte[] serializedMessage = new byte[1 + serializedKey.length + serializedValue.length];
		ByteBuffer buffer = ByteBuffer.wrap(serializedMessage);
		buffer.put(messageType);
		buffer.put(serializedKey);
		buffer.put(serializedValue);
		
		return serializedMessage;
	}
	
	public KVMessage unmarshal(byte[] bytes) throws Exception {
		int messageType = (int) bytes[0];
		KVMessage.StatusType statusType = KVMessage.StatusType.values()[messageType];
		String key = null;
		String value = null;
		
		int startPosition = 1;
		int terminatorPosition = getStringEnd(bytes, startPosition);
		if (terminatorPosition != startPosition) {
			try {
				key = deserializeString(Arrays.copyOfRange(bytes, startPosition, terminatorPosition));
			} catch (UnsupportedEncodingException e) {
				throw new Exception("The key cannot be decoded as " + CHARSET_NAME);
			}
		}
		
		startPosition = terminatorPosition + 1;
		terminatorPosition = getStringEnd(bytes, startPosition);
		if (terminatorPosition != startPosition) {
			try {
				value = deserializeString(Arrays.copyOfRange(bytes, startPosition, terminatorPosition));
			} catch (UnsupportedEncodingException e) {
				throw new Exception("The value cannot be decoded as " + CHARSET_NAME);
			}
		}
		
		return new KVMessageImpl(statusType, key, value);
	}
	
	private int getStringEnd(byte[] source, int startPosition) {
		int terminatorPosition = startPosition;
		int sourceSize = source.length;
		while (terminatorPosition < sourceSize && source[terminatorPosition] != 0) {
			++terminatorPosition;
		}
		return terminatorPosition;
	}
	
	private String deserializeString(byte[] bytes) throws UnsupportedEncodingException {
		return new String(bytes, CHARSET_NAME);
	}
	
	private byte[] serializeString(String toSerialize) throws UnsupportedEncodingException {
		if (toSerialize == null) {
			// When there is no string, only return the null-terminator
			// Make sure to return a new array so that possible modifications do not influence
			// the marshaller implementation...
			return Arrays.copyOf(TERMINATOR, TERMINATOR.length);
		}
		
		byte[] stringBytes = toSerialize.getBytes(CHARSET_NAME);

		byte[] ret = new byte[stringBytes.length + 1];
		ByteBuffer buffer = ByteBuffer.wrap(ret);
		buffer.put(stringBytes);
		buffer.put(TERMINATOR);
		
		return ret;
	}
	

	
}
