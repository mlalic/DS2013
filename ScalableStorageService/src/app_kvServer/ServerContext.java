package app_kvServer;

import java.util.concurrent.ConcurrentHashMap;

import common.metadata.MetaData;
import app_kvServer.KVServer.ServerStatusType;

public class ServerContext {

	private String nodeName;
	private ServerStatusType serverStatus;
	private MetaData metaData;
	private ConcurrentHashMap<String, String> serverStorage = new ConcurrentHashMap<String, String>();
	
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

	public ConcurrentHashMap<String, String> getServerStorage() {
		return serverStorage;
	}

	public void setServerStorage(ConcurrentHashMap<String, String> serverStorage) {
		this.serverStorage = serverStorage;
	}
	
}
