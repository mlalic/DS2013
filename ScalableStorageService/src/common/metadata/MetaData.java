package common.metadata;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.apache.log4j.Logger;

public class MetaData {
	
	private NavigableMap<String, ServerNode> serverRing = new TreeMap<String, ServerNode>();
	private static Logger logger = Logger.getRootLogger();

	/**
	 * Adds a new server node to the circle.
	 */
	public void addServer(ServerNode newNode) {
		logger.info("Adding new server node at position " + newNode.getHash());
		serverRing.put(newNode.getHash(), newNode);
	}
	
	/**
	 * Removes the given server node from the circle.
	 */
	public void removeServer(ServerNode nodeToRemove) {
		logger.info("Removing server node from position " + nodeToRemove.getHash());
		serverRing.remove(nodeToRemove.getHash());
	}
	
	/**
	 * Returns the node responsible for the given key.
	 * The KVClient library can use the method to find the server which
	 * it should contact.
	 * The KVServer can use the method to check whether it should process
	 * the request or send a NOT_RESPONSIBLE error along with the metadata
	 * (when the returned server does not match the one which is processing
	 *  the request, it is not responsible for the given key.)
	 * @param key The key for which to find the responsible server node
	 * @return The server node responsible for the given key
	 */
	public ServerNode getResponsibleServer(String key) {
		logger.info("Getting responsible server for " + key);
		if (serverRing.size() == 0) {
			// No node will be found anyway
			return null;
		}
		Entry<String, ServerNode> serverEntry = serverRing.higherEntry(key);
		if (serverEntry == null) {
			// The key is larger than any node in the cluster.
			// This means it is supposed to be mapped to the node with the
			// least hash magnitude, by wrapping around in a clock-wise fashion.
			serverEntry = serverRing.firstEntry();
		}
		return serverEntry.getValue();
	}

	/**
	 * Gets the server node which precedes the given node.
	 * @param node The node for which the predecessor should be returned
	 * @return
	 */
	public ServerNode getPredecessor(ServerNode node) {
		String key = node.getHash();
		logger.info("Getting predecessor for " + key);
		if (serverRing.size() == 0) {
			return null;
		}
		Entry<String, ServerNode> serverEntry = serverRing.lowerEntry(key);
		if (serverEntry == null) {
			serverEntry = serverRing.lastEntry();
		}
		return serverEntry.getValue();
	}
	
	/**
	 * @param node The node whose successor should be found
	 * @return A node which is the successor of the given node in the ring.
	 * 			<code>null</code> if the node isn't found in the ring and so can have no successor in the ring. 
	 */
	public ServerNode getSuccessor(ServerNode node) {
		final String nodeHash = node.getHash();
		if (!serverRing.containsKey(nodeHash)) {
			return null;
		}
		// The server responsible for the given node's hash is by definition of the "is-responsible"
		// predicate exactly the successor of the given node in the ring.
		return getResponsibleServer(nodeHash);
	}
	
	/**
	 * Returns a collection of all server nodes found in the current
	 * cluster.
	 */
	public Collection<ServerNode> getServers() {
		return serverRing.values();
	}
	
	/**
	 * @return A list of {@link ServerNode} instances representing nodes
	 * which serve as replication nodes for the key/value pairs of the given node.
	 * @param node The {@link ServerNode} for which the list of replicas should be returned 
	 */
	public List<ServerNode> getReplicas(ServerNode node) {
		List<ServerNode> replicas = new LinkedList<ServerNode>();
		replicas.add(getSuccessor(node));
		replicas.add(getSuccessor(getSuccessor(node)));
		return replicas;
	}

}
