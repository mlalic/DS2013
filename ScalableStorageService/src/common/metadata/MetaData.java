package common.metadata;

import java.util.HashMap;
import java.util.Map;

public class MetaData {
	
	private Map<String, ServerMetaData> servers = new HashMap<String, ServerMetaData>();
	
	public void addServer(String name, String ipAddress, int port, KeyRange range) {
		servers.put(name, new ServerMetaData(ipAddress, port, range));		
	}
	
	public void removeServer(String name) {
		servers.remove(name);
	}
	
	public void updateServer(String name, KeyRange range) {
		ServerMetaData existingData = servers.get(name);
		if (existingData == null) {
			return;
		}
		servers.put(name, new ServerMetaData(existingData.getIpAddress(), existingData.getPort(), range));
	}
	
	public ServerMetaData getServerMetaData(String name) {
		return servers.get(name);
	}

	public Map<String, ServerMetaData> getServers() {
		return servers;
	}
	
}
