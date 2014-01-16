package app_kvServer.commands;

import java.util.List;

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
	
	protected boolean isResponsibleFor(String key) {
		ServerNode responsibleServer = getResponsibleNode(key);
		if (responsibleServer == null) {
			return false;
		}
		return responsibleServer.getName().equals(serverContext.getNodeName());
	}

	private ServerNode getResponsibleNode(String key) {
		Md5Hasher hasher = new Md5Hasher();
		String hashedKey = hasher.getKeyHash(key);
		if (serverContext.getMetaData() == null) {		
			// This server doesn't know anything about the ring,
			// so it definitely shouldn't consider itself the responsible one
			return null;
		}
		return serverContext.getMetaData().getResponsibleServer(hashedKey);
	}

	protected boolean isReplicaFor(String key) {
		ServerNode responsibleNode = getResponsibleNode(key);
		return nodeInList(serverContext.getMetaData().getReplicas(responsibleNode));
	}

	private boolean nodeInList(List<ServerNode> replicas) {
		final ServerNode self = serverContext.getServerNode();
		for (ServerNode node: replicas) {
			if (node.equals(self)) {
				return true;
			}
		}
		return false;
	}
}
