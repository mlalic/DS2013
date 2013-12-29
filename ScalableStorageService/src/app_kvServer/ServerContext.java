package app_kvServer;

import java.util.HashMap;
import java.util.Map;

import app_kvServer.KVServer.ServerStatusType;
import common.metadata.MetaData;
import common.metadata.ServerNode;

public class ServerContext {

	private String nodeName;
	private ServerStatusType serverStatus;
	private MetaData metaData;
	private ServerStorage serverStorage = new InMemoryServerStorage();

	private Map<String, String> dirtyEntries = new HashMap<String, String>();
	private Object dirtyEntriesLock = new Object();
	
	public ServerContext(String nodeName, ServerStatusType serverStatus) {
		this.nodeName = nodeName;
		this.serverStatus = serverStatus;
	}

	public String getNodeName() {
		return nodeName;
	}
	
	public ServerNode getServerNode() {
		for (ServerNode node: metaData.getServers()) {
			if (node.getName().equals(nodeName)) {
				return node;
			}
		}
		// This server is not in the ring
		return null;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public ServerStatusType getServerStatus() {
		return serverStatus;
	}

	public void setServerStatus(ServerStatusType serverStatus) {
		this.serverStatus = serverStatus;
	}

	public MetaData getMetaData() {
		return metaData;
	}

	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	public ServerStorage getServerStorage() {
		return serverStorage;
	}

	public void setServerStorage(ServerStorage serverStorage) {
		this.serverStorage = serverStorage;
	}
	
	/**
	 * Track a new dirty entry in the context.
	 * 
	 * This method is thread safe (multiple threads can invoke it concurrently), as well
	 * as synchronized with {@link #getAndClearDirtyEntries()} (an intended insertion of
	 * a dirty entry will always be registered, it'll never end up lost due to a race
	 * condition on clearing the map).
	 * @param key
	 * @param value
	 */
	public void addDirtyEntry(String key, String value) {
		synchronized (dirtyEntriesLock) {
			dirtyEntries.put(key, value);
		}
	}

	/**
	 * Atomically gets a {@link Map} representing the dirty entries in the current context
	 * and clears the currently tracked list of dirt entries of the context.
	 * This guarantees that no {@link #addDiryEntry(String, String)} operations will be lost
	 * due to race conditions which could arise if the map could be modified concurrently with
	 * iteration. The operation will end up being reflected either in the map returned by the
	 * current {@link #getAndClearDirtyEntries()} call or the next one.
	 * @return A {@link Map} representing all dirty entries in the current context
	 */
	public Map<String, String> getAndClearDirtyEntries() {
		Map<String, String> ret = null;
		synchronized (dirtyEntriesLock) {
			// Keep a reference to the old object in order to return that object.
			// This way, we do not need to make a copy which the caller would get...
			ret = dirtyEntries;
			// ...since the context now references an empty map, effectively clearing the list of dirty entries.
			dirtyEntries = new HashMap<String, String>();
		}
		return ret;
	}

}
