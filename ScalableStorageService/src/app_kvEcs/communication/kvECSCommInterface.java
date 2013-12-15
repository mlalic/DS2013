package app_kvEcs.communication;

import common.messages.KVMessage;

public interface kvECSCommInterface {
    /**
     * Establishes a connection to the KV Server.
     * 
     * @throws Exception
     *             if connection could not be established.
     */
    public void connect(String ip, int port) throws Exception;

    /**
     * disconnects the client from the currently connected server.
     */
    public void disconnect();

    /**
     * Sends a message to a particular server
     * @param the message which is to be sent to the server.
     * @return a message that contains acknowledgement.
     */
    public KVMessage sendMessage(KVMessage message);
    
    /**
     * Set the name of the node which this connection represents
     * @param name the name of the host
     */
    public void setHostName(String name);
    
    /**
     * Returns the name of the node which this connection represents
     */
    public String getHostName();
}
