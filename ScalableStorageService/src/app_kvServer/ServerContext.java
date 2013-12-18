package app_kvServer;

import app_kvServer.KVServer.ServerStatusType;

import common.metadata.MetaData;

public class ServerContext {

	private String nodeName;
	private ServerStatusType serverStatus;
	private MetaData metaData;
	private ServerStorage serverStorage = new InMemoryServerStorage();
	
	public ServerContext(String nodeName, ServerStatusType serverStatus) {
		this.nodeName = nodeName;
		this.serverStatus = serverStatus;
	}

	public String getNodeName() {
		return nodeName;
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
	
}
