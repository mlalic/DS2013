package client;


import java.io.IOException;

import org.apache.log4j.Logger;

import client.communication.Session;
import client.communication.TcpSession;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.messages.KVMessageImpl;
import common.messages.KVMessageMarshaller;


public class KVStore implements KVCommInterface {
	private static Logger logger = Logger.getRootLogger();
	
    String host;
    int port;
    Session session;
	KVMessageMarshaller marshaller;
	/**
	 * Initialize KVStore with address and port of KVServer
	 * @param address the address of the KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String address, int port) {
		this.host = address;
		this.port = port;
		session = new TcpSession(this.host, this.port);
		marshaller = new KVMessageMarshaller();
	}
	
	/**
     * Establishes a connection to the KV Server.
     * @throws IOException/UnknownHostException 
     *             if connection could not be established.
     */
	public void connect() throws Exception {
	    session.connect();
	}
	
	/**
     * disconnects the client from the currently connected server.
     */
	public void disconnect() {
	    session.disconnect();		
	}

	/**
     * Inserts a key-value pair into the KVServer.
     * 
     * @param key
     *            the key that identifies the given value.
     * @param value
     *            the value that is indexed by the given key.
     * @return a message that confirms the insertion of the tuple or an error.
     * @throws Exception
     *             if put command cannot be executed (e.g. not connected to any
     *             KV server).
     */
	public KVMessage put(String key, String value) throws Exception {
		KVMessage message = new KVMessageImpl(StatusType.PUT, key, value);
		byte[] b = marshaller.marshal(message);
	    if(session!=null){
	    	logger.info(String.format(
	    			"Sending a put request for (key, value) pair - ('%s', '%s')",
	    			key, value)); 
	        session.send(b);
	        logger.info("Waiting for put request response...");
	        byte[] response = session.receive();
	        if (response == null) {
	        	logger.info("No response received");
	        	throw new Exception("Could not receive the response");
	        }
	        message = marshaller.unmarshal(response);
	        logger.info("Response received and processed");
	        return message;
	    }
	    else{
	        throw new NotConnectedException();
	    }
	}

	/**
     * Retrieves the value for a given key from the KVServer.
     * 
     * @param key
     *            the key that identifies the value.
     * @return the value, which is indexed by the given key.
     * @throws Exception
     *             if put command cannot be executed (e.g. not connected to any
     *             KV server).
     */
	public KVMessage get(String key) throws Exception {
		KVMessage message = new KVMessageImpl(StatusType.GET, key, null);
	    if(session!=null){
	    	logger.info(String.format(
	    			"Sending a get request for key '%s'",
	    			key)); 
	    	session.send(marshaller.marshal(message));
	        logger.info("Waiting for get request response...");
	        byte[] b = session.receive();
	        if (b == null) {
	        	logger.info("No response received");
	        	throw new Exception("Could not receive response");
	        }
	        message  = marshaller.unmarshal(b);
	        logger.info("Response received and processed");
	        return message;
	    }
	    else{
	        throw new NotConnectedException();
	    }
	}

	
}
