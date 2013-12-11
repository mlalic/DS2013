package common.metadata;

public class ServerNode {
	private String ipAddress;
	private int port;
	private transient final KeyHasher hasher = new Md5Hasher();

	public ServerNode(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	public String getName() {
		return ipAddress + ":" + port;
	}
	
	public String getHash() {
		return hasher.getKeyHash(getName()); 
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getPort() {
		return port;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ipAddress == null) ? 0 : ipAddress.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerNode other = (ServerNode) obj;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
	
}