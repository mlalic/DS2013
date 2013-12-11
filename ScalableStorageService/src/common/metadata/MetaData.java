package common.metadata;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;

public class MetaData {
	
	private NavigableMap<String, ServerNode> serverRing = new TreeMap<String, ServerNode>();

	/**
	 * Adds a new server node to the circle.
	 */
	public void addServer(ServerNode newNode) {
		serverRing.put(newNode.getHash(), newNode);
	}
	
	/**
	 * Removes the given server node from the circle.
	 */
	public void removeServer(ServerNode nodeToRemove) {
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
	 * Gets the server node which precedes the given key.
	 * @param key
	 * @return
	 */
	public ServerNode getPredecessor(String key) {
		if (serverRing.size() == 0) {
			return null;
		}
		// If the key is already found in the ring, it'll return the server
		// associated with that key.
		Entry<String, ServerNode> serverEntry = serverRing.floorEntry(key);
		if (serverEntry == null) {
			serverEntry = serverRing.lastEntry();
		}
		return serverEntry.getValue();
	}
	
	/**
	 * Returns a collection of all server nodes found in the current
	 * cluster.
	 */
	public Collection<ServerNode> getServers() {
		return serverRing.values();
	}

}
