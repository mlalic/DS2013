package testing;

import org.junit.Test;

import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.messages.KVMessageImpl;
import common.messages.KVMessageMarshaller;
import junit.framework.TestCase;

public class AdditionalTest extends TestCase {
	
	// TODO add your test cases, at least 3

	/**
	 * Tests that a message which is received by unmarshalling a previously marshalled message
	 * is the same as the original.
	 */
	@Test
	public void testMarshalThenUnmarshal() {
		KVMessage message = new KVMessageImpl(StatusType.PUT, "KEY", "This is a value");
		
		KVMessageMarshaller marshaller = new KVMessageMarshaller();
		byte[] marshalled;
		try {
			marshalled = marshaller.marshal(message);
		} catch (Exception e) {
			fail("Could not marshal the original message");
			return;
		}
		
		KVMessage unmarshalled;
		try {
			unmarshalled = marshaller.unmarshal(marshalled);
		} catch (Exception e) {
			fail("Could not unmarshal the marshalled bytes");
			return;
		}
		
		assertEquals(message.getKey(), unmarshalled.getKey());
		assertEquals(message.getValue(), unmarshalled.getValue());
		assertEquals(message.getStatus(), unmarshalled.getStatus());
	}
	
	/**
	 * Tests that marshalling/unmarshalling a message with no key works as expected.
	 */
	@Test
	public void testMarshalNoKey() {
		KVMessage message = new KVMessageImpl(StatusType.PUT, null, "Value");
		KVMessageMarshaller marshaller = new KVMessageMarshaller();
		byte[] marshalled;
		try {
			marshalled = marshaller.marshal(message);
		} catch (Exception e) {
			fail("Could not marshal the original message");
			return;
		}
		
		assertEquals((byte)0, marshalled[1]);
		
		KVMessage unmarshalled;
		try {
			unmarshalled = marshaller.unmarshal(marshalled);
		} catch (Exception e) {
			fail("Could not unmarshal the marshalled bytes");
			return;
		}

		assertEquals(message.getKey(), unmarshalled.getKey());		
		assertEquals(message.getKey(), unmarshalled.getKey());
		assertEquals(message.getValue(), unmarshalled.getValue());
	}
	
	/**
	 * Tests that unmarshalling a message with no key or value works as expected...
	 */
	@Test
	public void testMarshalNoValue() {
		KVMessage message = new KVMessageImpl(StatusType.PUT, null, null);
		
		KVMessageMarshaller marshaller = new KVMessageMarshaller();
		byte[] marshalled;
		try {
			marshalled = marshaller.marshal(message);
		} catch (Exception e) {
			fail("Could not marshal the original message");
			return;
		}
		
		KVMessage unmarshalled;
		try {
			unmarshalled = marshaller.unmarshal(marshalled);
		} catch (Exception e) {
			fail("Could not unmarshal the marshalled bytes");
			return;
		}
		
		assertEquals(message.getKey(), unmarshalled.getKey());
		assertEquals(message.getValue(), unmarshalled.getValue());
		assertEquals(message.getStatus(), unmarshalled.getStatus());
	}
}
