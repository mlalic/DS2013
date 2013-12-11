package common.metadata;

/**
 * A class holding all metadata related to a single server. 
 *
 */
public class ServerMetaData {
	
	private String ipAddress;
	private int port;
	private KeyRange range;
	
	public ServerMetaData(String ipAddress, int port, KeyRange range) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.range = range;
	}
	
	public ServerMetaData(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public KeyRange getRange() {
		return range;
	}

	public void setRange(KeyRange range) {
		this.range = range;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}
	
	

}
