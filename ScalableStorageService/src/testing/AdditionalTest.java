package testing;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import common.metadata.KeyHasher;
import common.metadata.KeyRange;
import common.metadata.Md5Hasher;
import common.metadata.MetaData;
import common.metadata.MetaDataTransport;
import common.metadata.ServerNode;

public class AdditionalTest extends TestCase {

	/**
	 * Tests that the {@link KeyRange#isInRange(String) isInRange} method
	 * runs correctly for "normal" ranges (where end > start).
	 */
	@Test
	public void testInRange() {
		final String start = "00";
		final String end = "AA";
		KeyRange range = new KeyRange(start, end);
		
		// A value obviously in the range
		assertTrue(range.isInRange("01"));
		// Start is included in the range
		assertTrue(range.isInRange(start));
		// End is not included in the range
		assertFalse(range.isInRange(end));
		// A value completely outside of the range
		assertFalse(range.isInRange("FF"));
		assertFalse(range.isInRange("AB"));
	}

	/**
	 * Tests that the {@link KeyRange#isInRange(String) isInRange} method
	 * runs correctly for ranges which "wrap around" the hash circle, i.e.
	 * end < start.
	 */
	@Test
	public void testInRangeBorder() {
		final String start = "CD";
		final String end = "10";
		
		KeyRange range = new KeyRange(start, end);
		
		// The start is considered in the range
		assertTrue(range.isInRange(start));
		// A value in the range
		assertTrue(range.isInRange("CE"));
		// The largest key value is also in the range
		assertTrue(range.isInRange("FF"));
		// ..as is the smallest
		assertTrue(range.isInRange("00"));
		// A value strictly less than the end is in the range
		assertTrue(range.isInRange("09"));
		// The end is considered outside of the range
		assertFalse(range.isInRange(end));
		// Values higher than the range's end are not in it.
		assertFalse(range.isInRange("11"));
		// A value strictly less than the range's beginning is not in it.
		assertFalse(range.isInRange("CC"));
	}
	
	@Test
	public void testMetaData() {
		MetaData metaData = new MetaData();
		List<ServerNode> servers = new LinkedList<ServerNode>();
		servers.add(new ServerNode("192.168.1.2", 50));
		servers.add(new ServerNode("192.168.1.1", 50));
		for (ServerNode node: servers) {
			metaData.addServer(node);
		}

		assertEquals(2, metaData.getServers().size());
		assertEquals(servers, new LinkedList<ServerNode>(metaData.getServers()));
	}
	
	@Test
	public void testMarshalMetaDataToJson() {
		MetaData metaData = new MetaData();
		
		metaData.addServer(new ServerNode("192.168.1.1", 10000));
		metaData.addServer(new ServerNode("192.168.1.1", 50001));
		List<ServerNode> originalServers = new LinkedList<ServerNode>(metaData.getServers());
		String json = MetaDataTransport.marshalMetaData(metaData);

		MetaData deserialized = MetaDataTransport.unmarshalMetaData(json);

		assertEquals(2, deserialized.getServers().size());
		assertEquals(originalServers, new LinkedList<ServerNode>(deserialized.getServers()));
	}
	
	/**
	 * Test the situation when the received string value cannot be
	 * unmarshalled back to a MetaData instance.
	 */
	@Test
	public void testUnmarshalInvalidMetaData() {
		String json = "Some invalid json string...";
		
		MetaData deserialized = MetaDataTransport.unmarshalMetaData(json);
		
		assertNull(deserialized);
	}
	
	/**
	 * Tests for the {@link Md5Hasher}
	 */
	@Test
	public void testMd5Hasher() {
		KeyHasher hasher = new Md5Hasher();

		// Min value is 32 zeroes
		final String minValue = hasher.getMinHash();
		assertEquals(32, minValue.length());
		for (int i = 0; i < 32; ++i) assertTrue(minValue.charAt(i) == '0');
		
		// Max value is 32 Fs
		final String maxValue = hasher.getMaxHash();
		assertEquals(32, minValue.length());
		for (int i = 0; i < 32; ++i) assertTrue(maxValue.charAt(i) == 'F');
		
		// A well known hash?
		final String hash = hasher.getKeyHash("key");
		assertEquals("3c6e0b8a9c15224a8228b9a98ca1531d", hash);
	}
}
