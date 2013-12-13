package app_kvServer.commands;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import common.messages.*;
import common.metadata.*;

public class GetServerCommand extends ServerCommand {
	
	private static Logger logger = Logger.getRootLogger();
	
	protected ConcurrentHashMap<String, String> serverStorage;
	private ServerStatusType serverStatus;
	private String nodeName;
	private MetaData metaData;

	public GetServerCommand(String key, String value, final ConcurrentHashMap<String, String> serverStorage, 
			ServerStatusType serverStatus, String nodeName, MetaData mataData) {
		super(key, value);
		this.serverStorage = serverStorage;
		this.serverStatus = serverStatus;
		this.nodeName = nodeName;
		this.metaData = mataData;
	}

	/**
	 * Get the message from the serverStorage with the given name
	 * If we find a message with the given key, then status is GET_SUCCESS
	 * If we don't find a message with the given name then staus is GET_ERROR 
	 */
	@Override
	public KVMessage execute() {
		if (serverStatus.equals(ServerStatusType.STARTED) || serverStatus.equals(ServerStatusType.LOCKED_WRITE)) {
			ServerNode responsibleSever = metaData.getResponsibleServer(key);
			if (responsibleSever.getName().equals(nodeName)) {
				String value = serverStorage.get(key);
				if (value == null) {
					responseMessage = new KVMessageImpl(
							KVMessage.StatusType.GET_ERROR,
							key,
							"Key '" + key + "' not found");
					logger.info("Server has executed GET_ERROR");
					return responseMessage;
				}
				else {
					responseMessage = new KVMessageImpl(KVMessage.StatusType.GET_SUCCESS, key, value);
					logger.info("Server has executed GET_SUCCESS");
					return responseMessage;
				}
			} else {
				// If the server is not responsible for that range, it is sending new updated meta data to the client
				String transportMetaData = MetaDataTransport.marshalMetaData(metaData);
				responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_NOT_RESPONSIBLE, key, transportMetaData);
				logger.info("Server is not responsible for that key range. Updated meta data has been sent.");
				return responseMessage;
			}
		} else if (serverStatus.equals(ServerStatusType.STOPPED)){
			responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_STOPPED, key, value);
			logger.info("Sever is stopped for serving requests.");
			return responseMessage;
		} else {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_STOPPED, key, value);
			logger.info("Srver is not longer in thw ring, therefore he is not responsible any more.");
			return responseMessage;
		}
	}

	@Override
	public boolean isValid() {
		if (key == null) {
			return false;
		}
		return true;
	}

}
