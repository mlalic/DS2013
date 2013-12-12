package client;


import java.io.IOException;

import org.apache.log4j.Logger;

import common.communication.Session;
import common.communication.TcpSession;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.messages.KVMessageImpl;
import common.messages.KVMessageMarshaller;
import common.metadata.KeyHasher;
import common.metadata.Md5Hasher;
import common.metadata.MetaData;
import common.metadata.MetaDataTransport;
import common.metadata.ServerNode;


public class KVStore implements KVCommInterface {
	private static Logger logger = Logger.getRootLogger();
	
	private final KVMessageMarshaller marshaller = new KVMessageMarshaller();
	private final KeyHasher hasher = new Md5Hasher();
	
	private MetaData metaData;
	/**
	 * Initialize KVStore with the address and port of the initial KVServer
	 * @param address the host name/IP address of the initial KVServer
	 * @param port the port of the KVServer
	 */
	public KVStore(String host, int port) {
		// Form the initial metadata as noticed by this client.
		// It thinks that the initial server is responsible for the entire
		// key space.
		// However, on first get/put operation, the client will get the updated
		// metadata from the initial server and automatically find the correct
		// server node to connect to.
		metaData = new MetaData();
		metaData.addServer(new ServerNode(host, port));
	}
	
	/**
     * Establishes a connection to the KV Server.
     * @throws IOException/UnknownHostException 
     *             if connection could not be established.
     */
	public void connect() throws Exception {
	    // TODO Define the semantics of a connect method call in the context of a distributed store
	}
	
	/**
     * disconnects the client from the KVServer node cloud.
     */
	public void disconnect() {
		// TODO Define the semantics of a disconnect method call in the context of a distributed store
	    // This effectively means purging all metadata known to the client so that it can
		// no longer perform any operations.
		metaData = new MetaData();
	}

	/**
     * Inserts a key-value pair into the KVServer cloud.
     * 
     * @param key
     *            the key that identifies the given value.
     * @param value
     *            the value that is indexed by the given key.
     * @return a message that confirms the insertion of the tuple or an error.
     * @throws Exception
     *             if the put command cannot be executed (e.g. not connected to any
     *             KV server).
     */
	public KVMessage put(String key, String value) throws Exception {
		// TODO: Refactor to a class field (that can be set at construction time).
		final int PUT_MAX_RETRY_COUNT = 1000;
		// Prebuild the put message which will be sent to the server nodes.
		// It is independent of the node to which it is sent, so there is no need
		// to rebuild it each time a new node is contacted.
		final KVMessage putMessage = buildPutMessage(key, value);

		logger.info(String.format(
				"Performing a put request for (key, value) pair - ('%s', '%s')",
				key, value));
		
		return sendMessageToCorrectNode(key, PUT_MAX_RETRY_COUNT, putMessage);
	}

	/**
     * Retrieves the value for a given key from the KVServer cloud.
     * 
     * @param key
     *            the key that identifies the value.
     * @return the value, which is indexed by the given key.
     * @throws Exception
     *             if the get command cannot be executed (e.g. not connected to any
     *             KV server).
     */
	public KVMessage get(String key) throws Exception {
		// TODO: Refactor to a class field (that can be set at construction time).
		final int GET_MAX_RETRY_COUNT = 1000;
		// Prebuild the put message which will be sent to the server nodes.
		// It is independent of the node to which it is sent, so there is no need
		// to rebuild it each time a new node is contacted.
		final KVMessage getMessage = buildGetMessage(key);

		logger.info(String.format(
				"Performing a get request for key '%s'",
				key)); 
		
		return sendMessageToCorrectNode(key, GET_MAX_RETRY_COUNT, getMessage);
	}

	/**
	 * A private helper method which sends the given message to the correct server,
	 * based on the given key.
	 * It takes care of updating the cached metadata and retrying based on the newly
	 * received metadata, when it receives a SERVER_NOT_RESPONSIBLE response.
	 *
	 * This method does all the heavy lifting necessary to implement the consistent
	 * hashing scheme on the client side, with the {@link #get(String)} and {@link #put(String, String)}
	 * methods simply delegating to it, by giving the message that should be sent.
	 * 
	 * @param key The key based on which the server to be contacted is to be determined
	 * @param maxRetryCount The maximum number of times the method should attempt to
	 * 						communicate with a server before giving up and throwing an
	 * 						exception.
	 * @param message The message which should be sent to the server
	 * @return The response returned by the eventual node which is in charge of the
	 * 			given key, represented as a {@link KVMessage} instance.
	 * @throws Exception When the client loses all metadata (and thereby becomes unable
	 * 						to communicate with any server, even to update its metadata).
	 * 					 When the maximum number of retries is reached.
	 */
	private KVMessage sendMessageToCorrectNode(String key, final int maxRetryCount,
			final KVMessage message) throws Exception {
		for (int tryNumber = 1; tryNumber <= maxRetryCount; ++tryNumber) {
			// First, locate the server node which is in charge for storing the given
			// key.
			ServerNode responsibleNode = getResponsibleNode(key);
			if (responsibleNode == null) {
				// The client no longer has any associated metadata for some reason.
				// It cannot get an update since it does not know which server to contact.
				// It can no longer service the KV interface requests.
				// TODO: This shouldn't really be called a "cluster", should it?
				logger.error("Unable to process the request - no metadata");
				throw new Exception("Lost connection to all servers in the KVServer cluster");
			}

			logger.info(String.format(
					"Trying to send a message %s. try #%d, responsible node: %s",
					message.getStatus().toString(),
					tryNumber,
					responsibleNode.getName()));
			
			// Try contacting this node
			KVMessage response = sendToNode(message, responsibleNode);
			if (response == null) {
				// No response received.
				// TODO Try again or propagate the error to the client?

			} else if (response.getStatus() == StatusType.SERVER_NOT_RESPONSIBLE) {
				logger.info("Node was not responsible. Updating metadata...");
				updateMetaData(response.getValue());
			} else if (response.getStatus() == StatusType.SERVER_STOPPED) {
				// TODO Try again in a while or propagate the error to the client?
				
			} else if (response.getStatus() == StatusType.SERVER_WRITE_LOCK) {
				// TODO Try again in a while or propagate the error to the client?
				
			} else {
				// All other responses indicate that the correct node was contacted and
				// the operation was performed.
				// The clients should handle all other kinds of possible errors, such as
				// PUT_ERROR, etc.
				logger.info("Successfully received response from a responsible node.");
				return response;
			}
		}
		// The maximum retry count was reached without successfully contacting
		// the responsible node.
		logger.error(String.format(
				"Maximum retry count reached for request %s",
				message.getStatus().toString()));
		throw new Exception(
				"Maximum retry count reached without getting a response from any server node");
	}

	/**
	 * Private helper method which performs an update of the metadata currently
	 * associated with the client instance based on the given JSON representation
	 * of new metadata. 
	 * @param jsonMetaData A JSON-encoded string representing the new metadata
	 */
	private void updateMetaData(String jsonMetaData) {
		MetaData newMetaData = MetaDataTransport.unmarshalMetaData(jsonMetaData);
		if (newMetaData != null) {
			metaData = newMetaData;
			// TODO If the received metadata is in an invalid format should the client:
			//    1. Throw away the received data and keep the old cached data
			//    2. Go into a "terminated" state where it can no longer communicate with
			//       any KVServer.
			// For now, the old metadata is kept, in hopes of getting a better response
			// in one of the subsequent tries.
		} else {
			logger.error("Malformed metadata received. Keeping the old cached metadata.");
		}
	}
	
	/**
	 * A private helper method which sends the given message to the given server node
	 * and returns the received response, if any.
	 * @param message A {@link KVMessage} to be sent to the given server node.
	 * @param node The {@link ServerNode} to which the message should be sent
	 * @return The node's response as a {@link KVMessage}
	 */
	private KVMessage sendToNode(KVMessage message, ServerNode node) {
		// First establish a connection to the node
		logger.info(String.format("Establishing a connection to %s", node.getName()));
		Session session = connectToNode(node);
		if (session == null) {
			// Unable to establish a connection to the node...
			logger.error(String.format(
					"Unable to establish a connection to %s", node.getName()));
			return null;
		}
		// Then send the message and return the received response
		try {
			logger.info(String.format("Sending message to %s", node.getName()));
			sendMessage(message, session);
		} catch (IOException e) {
			// Cannot communicate with the given node -- no response will be received.
			return null;
		}
		logger.info(String.format("Waiting for response from %s", node.getName()));
		final KVMessage response = receiveMessage(session);
		logger.info(String.format("Response received from %s", node.getName()));
		// Terminate the session to avoid a resource leak
		// TODO Implement a client-side session cache? Before instantiating a new session, try to reuse the cached one. If the server has closed the socket, reconnect and try again. May increase the efficiency of client-side requests, but could potentially cause too many communication sockets to stay open on the server side (would require a scheme to terminate inactive client sockets on the server side after a certain amount of time).
		session.disconnect();
		return response;
	}

	/**
	 * Helper private method which receives a response from the given {@link Session}
	 * instance and returns it as a {@link KVMessage}.
	 * @param session The {@link Session} which should be used to read the incoming message
	 * @return A {@link KVMessage} instance representing the received response.
	 * 			<code>null</code> when there is no received message
	 */
	private KVMessage receiveMessage(Session session) {
		byte[] bytes;
		try {
			bytes = session.receive();
		} catch (IOException e1) {
			// Cannot communicate over the given session.
			return null;
		}
		if (bytes == null) {
			return null;
		}
		try {
			return marshaller.unmarshal(bytes);
		} catch (Exception e) {
			// Malformed/invalid response message -- communication between the
			// server node and the client is no longer possible.
			return null;
		}
	}

	/**
	 * Private helper method which builds a {@link KVMessage} instance which
	 * requests the given key, value pair to be put.
	 * @param key The key for which the value should be put
	 * @param value The value associated with the key
	 * @return A {@link KVMessage} instance representing this request
	 */
	private KVMessage buildPutMessage(String key, String value) {
		return new KVMessageImpl(StatusType.PUT, key, value);
	}

	/**
	 * Private helper method which builds a {@link KVMessage} instance which
	 * requests the value associated to the given key
	 * @param key The key for which the value should be retrieved
	 * @return A {@link KVMessage} instance representing this request
	 */
	private KVMessage buildGetMessage(String key) {
		return new KVMessageImpl(StatusType.GET, key, null);
	}

	/**
	 * Private helper method which returns the {@link ServerNode} instance which is
	 * responsible for the given key based on the current metadata known to the client.
	 * @param key The key based on which the {@link ServerNode} should be determined.
	 * @return The responsible {@link ServerNode}
	 */
	private ServerNode getResponsibleNode(String key) {
		String keyHash = hasher.getKeyHash(key);
		return metaData.getResponsibleServer(keyHash);
	}
	
	/**
	 * Private helper method which sends the given message over the given open
	 * {@link Session} instance.
	 * @param message The message to be sent to the server node
	 * @param session The {@link Session} over which to send the message.
	 * 					<bold>The session instance must be in the connected state.</bold>
	 * @throws IOException When the message send fails due to a communication error.
	 */
	private void sendMessage(KVMessage message, Session session) throws IOException {
		byte[] messageAsBytes = marshalMessage(message);
		session.send(messageAsBytes);
	}
	
	/**
	 * Private helper method which returns a byte array representing a {@link KVMessage}
	 * which is suitable for "over the wire" transfer.
	 * The method wraps the possible exception which the {@link KVMessageMarshaller#marshal(KVMessage)}	
	 * method can throw and returns null instead.
	 * @param message The message to be encoded to the byte array
	 * @return a byte array representing the given message
	 */
	private byte[] marshalMessage(KVMessage message) {
		try {
			return marshaller.marshal(message);
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Private helper method which establishes a connection with a
	 * server node.
	 * @param node The node to which the client should connect to
	 * @return A {@link Session} instance which can be used to communicate
	 * 	  	   with the server node.
	 */
	private Session connectToNode(ServerNode node) {
		Session session = new TcpSession(node.getIpAddress(), node.getPort());
		try {
			session.connect();
		} catch (IOException e) {
			return null;
		}

		return session;
	}

}
