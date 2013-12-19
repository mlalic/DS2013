package app_kvEcs.communication;

import common.messages.KVMessage;
import common.metadata.ServerNode;

public abstract class NodeCommunicator {
	protected ServerNode node;
	
	public NodeCommunicator(ServerNode node) {
		this.node = node;
	}
	
    /**
     * Establishes a connection to the KV Server.
     * 
     * @throws Exception
     *             if connection could not be established.
     */
    public abstract void connect() throws Exception;

    /**
     * disconnects the client from the currently connected server.
     */
    public abstract void disconnect();

    /**
     * Sends a message to a particular server
     * @param the message which is to be sent to the server.
     * @return a message that contains acknowledgement.
     */
    public abstract KVMessage sendMessage(KVMessage message);

    /**
     * Returns a {@link ServerNode} instance with which this
     * {@link NodeCommunicator} is communicating.
     * @return
     */
    public ServerNode getServerNode() {
    	return this.node;
    }
}
