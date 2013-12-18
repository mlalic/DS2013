package app_kvServer.commands;

import app_kvServer.ServerContext;
import common.messages.*;
import common.metadata.Md5Hasher;
import common.metadata.ServerNode;

public abstract class ServerCommand {
	
	protected String key;
	protected String value;
	protected KVMessage responseMessage = null;
	protected ServerContext serverContext;
	
	public ServerCommand(String key, String value, final ServerContext serverContext) {
		this.key = key;
		this.value = value;
		this.serverContext = serverContext;
	}
	
	public abstract KVMessage execute();
	
	public abstract boolean isValid();
	
	public boolean isResponsibleFor(String key) {
		Md5Hasher hasher = new Md5Hasher();
		String hashedKey = hasher.getKeyHash(key);
		if (serverContext.getMetaData() == null) {		
			// This server doesn't know anything about the ring,
			// so it definitely shouldn't consider itself the responsible one
			return false;
		}
		ServerNode responsibleSever = serverContext.getMetaData().getResponsibleServer(hashedKey);
		return responsibleSever.getName().equals(serverContext.getNodeName());
	}
}
