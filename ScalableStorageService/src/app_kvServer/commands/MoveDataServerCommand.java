package app_kvServer.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import app_kvServer.ServerContext;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import common.communication.NodeCommunicator;
import common.communication.TcpNodeCommunicator;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.messages.KVMessageImpl;
import common.metadata.KeyHasher;
import common.metadata.KeyRange;
import common.metadata.Md5Hasher;
import common.metadata.ServerNode;

public class MoveDataServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
		
	public MoveDataServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		ServerNode destinationNode = getNodeFromValue(value);
		logger.info("Moving data to the node " + destinationNode.getNodeAddress());

		final Map<String, String> dataToMove = getDataToMove(destinationNode);

		boolean success = false;
		try {
			success = sendData(dataToMove, destinationNode);
		} catch (Exception e) {
			// Send failed...
		}
		
		if (success) {
			return new KVMessageImpl(StatusType.ACK);
		} else {
			return new KVMessageImpl(StatusType.COMM_ERROR);
		}
	}

	/**
	 * Private helper method which builds the map of key/value pairs which should be
	 * moved from the current server to the given destination server node.
	 * @param destinationNode The destination server node to which data should be moved
	 * @return A {@link Map} representing data which should be moved
	 */
	private Map<String, String> getDataToMove(ServerNode destinationNode) {
		final KeyRange range = buildRange(destinationNode);
		logger.info(String.format(
				"Building a map of data to move. All data in the range [%s, %s) will be moved.",
				range.getStart(),
				range.getEnd()));
		final Map<String, String> dataToMove = new HashMap<String, String>();

		final KeyHasher hasher = new Md5Hasher();
		for (Entry<String, String> entry: serverContext.getServerStorage().getAllData().entrySet()) {
			if (range.isInRange(hasher.getKeyHash(entry.getKey()))) {
				dataToMove.put(entry.getKey(), entry.getValue());
			}
		}
		return dataToMove;
	}

	/**
	 * Private helper method which builds the {@link KeyRange} instance representing
	 * the range of keys which should be moved from the current server to the destination
	 * node.
	 * @param destinationNode The node to which data should be moved from the current node
	 * @return The range of data which should be moved
	 */
	private KeyRange buildRange(ServerNode destinationNode) {
		ServerNode self = serverContext.getServerNode();
		ServerNode predecessor = serverContext.getMetaData().getPredecessor(self);
		return new KeyRange(predecessor.getHash(), destinationNode.getHash());
	}

	/**
	 * Private helper method which sends the given data to the given destination server node
	 * @param packedData The data to be sent
	 * @param destinationNode 
	 * @return
	 * @throws Exception
	 */
	private boolean sendData(Map<String, String> data, ServerNode destinationNode) throws Exception {
		String packedData = packData(data);
		KVMessageImpl message = new KVMessageImpl(
				StatusType.BULK_DATA_MOVE,
				"data",
				packedData);
		NodeCommunicator communicator = new TcpNodeCommunicator(destinationNode);
		communicator.connect();
		
		KVMessage response = communicator.sendMessage(message);
		if (response == null) {
			return false;
		}
		return response.getStatus() == StatusType.ACK;
	}

	/**
	 * Private helper method which packs the given data which is to be transported to
	 * a different server node into a format which is suitable for transfer in a
	 * {@link KVMessage} value field
	 * @param data The data to be moved to the other server
	 * @return A string representing the given data
	 */
	private String packData(Map<String, String> data) {
		Gson gson = new Gson();
		return gson.toJson(data);
	}

	/**
	 * Private helper method deserializing the received server node into a {@link ServerNode} instance.
	 * @param jsonValue
	 * @return
	 */
	private ServerNode getNodeFromValue(String jsonValue) {
		Gson gson = new Gson();
		try {
			return gson.fromJson(jsonValue, ServerNode.class);
		} catch (JsonSyntaxException exc) {
			return null;
		}
	}

	@Override
	public boolean isValid() {
		return serverContext.getMetaData() != null; 
	}

}
