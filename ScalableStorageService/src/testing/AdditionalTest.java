package testing;

import java.util.ArrayList;
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
		metaData.addServer(new ServerNode("node2", "192.168.1.1", 50001));
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

		// A well known hash is correct?
		final String hash = hasher.getKeyHash("key");
		assertEquals("3c6e0b8a9c15224a8228b9a98ca1531d", hash);
	}
	
	@Test
	public void testGetResponsibleServerSimple() {
		MetaData metaData = new MetaData();
		ServerNode server1 = new ServerNode("192.168.1.2", 50);
		ServerNode server2 = new ServerNode("192.168.1.1", 50);
		metaData.addServer(server1);
		metaData.addServer(server2);
		
		// When there are only two servers in the ring, the server responsible
		// for a server's hash value is the other server in the ring
		assertEquals(server2, metaData.getResponsibleServer(server1.getHash()));
		assertEquals(server1, metaData.getResponsibleServer(server2.getHash()));
	}
	
	/**
	 * When there is only one server in the ring, it is responsible for all keys
	 */
	@Test
	public void testGetResponsibleServerSingleServer() {
		MetaData metaData = new MetaData();
		ServerNode server = new ServerNode("192.168.1.1", 50);
		metaData.addServer(server);
		
		// It is responsible for its own hash! (in all other cases, the server is not responsible for its own hash value).
		assertEquals(server, metaData.getResponsibleServer(server.getHash()));
		// Responsible for a random key
		assertEquals(server, metaData.getResponsibleServer("3c6e0b8a9c15224a8228b9a98ca1531d"));
		// Responsible for the largest and smallest hash
		assertEquals(server, metaData.getResponsibleServer("00000000000000000000000000000000"));
		assertEquals(server, metaData.getResponsibleServer("ffffffffffffffffffffffffffffffff"));
	}
	
	@Test
	public void testGetResponsibleServerMultipleServers() {
		MetaData metaData = new MetaData();
		ArrayList<ServerNode> servers = new ArrayList<ServerNode>();
		servers.add(new ServerNode("192.168.1.2", 50));
		servers.add(new ServerNode("192.168.1.100", 50000));
		servers.add(new ServerNode("192.168.1.1", 50));
		for (ServerNode node: servers) {
			metaData.addServer(node);
		}
		
		// Smallest possible key -> should be associated to the first server in the ring
		assertEquals(servers.get(0), metaData.getResponsibleServer("00000000000000000000000000000000"));
		// Key smaller than the hash of server[0] by 1 -> should be associated to server[0]
		assertEquals(servers.get(0), metaData.getResponsibleServer("29bcf7cebcb26607d08fd0ea83ccfea3"));
		// Key equal to hash of server[0] -> by convention it is mapped to the next server in the ring
		assertEquals(servers.get(1), metaData.getResponsibleServer("29bcf7cebcb26607d08fd0ea83ccfea4"));
		// Key greater than the hash of server[0] by 1 -> should associated to server[1]
		assertEquals(servers.get(1), metaData.getResponsibleServer("29bcf7cebcb26607d08fd0ea83ccfea5"));
		// Key easily in the range between [hash(server[0]), hash(server[1])) -> mapped to server[1]
		assertEquals(servers.get(1), metaData.getResponsibleServer("3c6e0b8a9c15224a8228b9a98ca1531d"));
		// Key lesser than hash(server[1]) by 1 -> mapped to server[1]
		assertEquals(servers.get(1), metaData.getResponsibleServer("542f448eaa4b54a08c211a07c2d0438e"));
		// Key equal to hash(server[1]) -> mapped to server[2] (half open interval on the right)
		assertEquals(servers.get(2), metaData.getResponsibleServer("542f448eaa4b54a08c211a07c2d0438f"));		
		// Key greater than the hash of server[1] by 1 -> mapped to server[2]
		assertEquals(servers.get(2), metaData.getResponsibleServer("542f448eaa4b54a08c211a07c2d04390"));
		// Key easily in [hash(server[1], hash(server[2])) -> mapped to server[2]
		assertEquals(servers.get(2), metaData.getResponsibleServer("9fffffffffffffffffffffffffffffff"));
		// Key equal to hash(server[2]) -> mapped to server[0] (wrap around!)
		assertEquals(servers.get(0), metaData.getResponsibleServer("ff3daeb0db7cbb8afe90d176338c765b"));
		// Key greater than hash(server[2]) by 1 -> mapped to server[0] (wrap around!)
		assertEquals(servers.get(0), metaData.getResponsibleServer("ff3daeb0db7cbb8afe90d176338c765c"));
		// Key easily in range [hash(server[2]), hash(server[0])) -> mapped to server[0]
		assertEquals(servers.get(0), metaData.getResponsibleServer("fffdaeb0db7cbb8afe90d176338c765b"));
		// Largest possible key is in [hash(server[2]), hash(server[0])) -> mapped to server[0]
		assertEquals(servers.get(0), metaData.getResponsibleServer("ffffffffffffffffffffffffffffffff"));
		
	}
	
	/**
	 * The predecessor of a given hash is defined to be exactly the server
	 * preceding the one that the hash would be assigned to.
	 * Edge case is when the given hash equals a server's hash -- in that case
	 * the correct answer is that same server, since the key would be assigned
	 * to the next server in the ring.
	 * 
	 * We reuse the ring probe values from {@link #testGetResponsibleServerMultipleServers()}
	 * in this test and check whether the returned value is the one that precedes the
	 * server from that test.
	 */
	@Test
	public void testGetPredecessorNode() {
		MetaData metaData = new MetaData();
		ArrayList<ServerNode> servers = new ArrayList<ServerNode>();
		servers.add(new ServerNode("192.168.1.2", 50));
		servers.add(new ServerNode("192.168.1.100", 50000));
		servers.add(new ServerNode("192.168.1.1", 50));
		for (ServerNode node: servers) {
			metaData.addServer(node);
		}
		
		// NOTE:
		// The return value of getPredecessor should always be the server that comes
		// before the server to which the key hash would be assigned to!
		
		// Smallest possible key -> should be associated to the first server in the ring
		assertEquals(servers.get(2), metaData.getPredecessor("00000000000000000000000000000000"));
		// Key smaller than the hash of server[0] by 1 -> should be associated to server[0]
		assertEquals(servers.get(2), metaData.getPredecessor("29bcf7cebcb26607d08fd0ea83ccfea3"));
		// Key equal to hash of server[0] -> by convention it is mapped to the next server in the ring
		assertEquals(servers.get(0), metaData.getPredecessor("29bcf7cebcb26607d08fd0ea83ccfea4"));
		// Key greater than the hash of server[0] by 1 -> should associated to server[1]
		assertEquals(servers.get(0), metaData.getPredecessor("29bcf7cebcb26607d08fd0ea83ccfea5"));
		// Key easily in the range between [hash(server[0]), hash(server[1])) -> mapped to server[1]
		assertEquals(servers.get(0), metaData.getPredecessor("3c6e0b8a9c15224a8228b9a98ca1531d"));
		// Key lesser than hash(server[1]) by 1 -> mapped to server[1]
		assertEquals(servers.get(0), metaData.getPredecessor("542f448eaa4b54a08c211a07c2d0438e"));
		// Key equal to hash(server[1]) -> mapped to server[2] (half open interval on the right)
		assertEquals(servers.get(1), metaData.getPredecessor("542f448eaa4b54a08c211a07c2d0438f"));		
		// Key greater than the hash of server[1] by 1 -> mapped to server[2]
		assertEquals(servers.get(1), metaData.getPredecessor("542f448eaa4b54a08c211a07c2d04390"));
		// Key easily in [hash(server[1], hash(server[2])) -> mapped to server[2]
		assertEquals(servers.get(1), metaData.getPredecessor("9fffffffffffffffffffffffffffffff"));
		// Key equal to hash(server[2]) -> mapped to server[0] (wrap around!)
		assertEquals(servers.get(2), metaData.getPredecessor("ff3daeb0db7cbb8afe90d176338c765b"));
		// Key greater than hash(server[2]) by 1 -> mapped to server[0] (wrap around!)
		assertEquals(servers.get(2), metaData.getPredecessor("ff3daeb0db7cbb8afe90d176338c765c"));
		// Key easily in range [hash(server[2]), hash(server[0])) -> mapped to server[0]
		assertEquals(servers.get(2), metaData.getPredecessor("fffdaeb0db7cbb8afe90d176338c765b"));
		// Largest possible key is in [hash(server[2]), hash(server[0])) -> mapped to server[0]
		assertEquals(servers.get(2), metaData.getPredecessor("ffffffffffffffffffffffffffffffff"));
	}
	
}
