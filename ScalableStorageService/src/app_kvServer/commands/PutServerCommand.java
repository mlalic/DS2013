package app_kvServer.commands;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import common.messages.*;
import common.metadata.*;

public class PutServerCommand extends ServerCommand {
	
	private static Logger logger = Logger.getRootLogger();
	
	protected ConcurrentHashMap<String, String> serverStorage;
	private ServerStatusType serverStatus;
	private String nodeName;
	private MetaData metaData;
	
	public PutServerCommand(String key, String value, final ConcurrentHashMap<String, String> serverStorage, 
			ServerStatusType serverStatus, String nodeName, MetaData metaData) {
		super(key, value);
		this.serverStorage = serverStorage;
		this.serverStatus = serverStatus;
		this.nodeName = nodeName;
		this.metaData = metaData;
	}

	/**
	 *  Put the message with the given key in the serverStorage
	 *  If it is first time to put the message with the given key then status is PUT_SUCCESS
	 *  If there is a message with the given key, then status is PUT_UPDATE
	 *  If there is some problem with getting and putting values then status of response is PUT_ERROR
	 */
	@Override
	public KVMessage execute() {
		if (serverStatus.equals(ServerStatusType.STARTED)) {
			ServerNode responsibleServer = metaData.getResponsibleServer(nodeName);
			if (responsibleServer.getName().equals(nodeName)) {
				try {
					boolean oldValue = false;
					oldValue = serverStorage.containsKey(key);
					if (!oldValue) {
						serverStorage.put(key, value);
						responseMessage = new KVMessageImpl(KVMessage.StatusType.PUT_SUCCESS, key, value);
						logger.info("Server has executed PUT_SUCCESS");
						return responseMessage;
					}
					else if ("null".equals(value)){
						try {
							serverStorage.remove(key);
							responseMessage = new KVMessageImpl(KVMessage.StatusType.DELETE_SUCCESS, key, value);
							logger.info("Server has executed DELETE_SUCCESS");
							return responseMessage;
						}
						catch (Exception e) {
							responseMessage = new KVMessageImpl(KVMessage.StatusType.DELETE_ERROR, key, value);
							logger.info("Server has executed DELETE_ERROR");
							return responseMessage;
						}
					}
					else {
						serverStorage.put(key, value);
						responseMessage = new KVMessageImpl(KVMessage.StatusType.PUT_UPDATE, key, value);
						logger.info("Server has executed PUT_UPDATE");
						return responseMessage;
					}
				} catch (Exception e) {
					responseMessage = new KVMessageImpl(KVMessage.StatusType.PUT_ERROR, key, e.getMessage());
					logger.info("Server has executed PUT_ERROR");
					return responseMessage;
				}
			}
			else {
				// If the server is not responsible for that range, it is sending new updated meta data to the client
				String transportMetaData = MetaDataTransport.marshalMetaData(metaData);
				responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_NOT_RESPONSIBLE, key, transportMetaData);
				logger.info("Server is not responsible for that key range. Updated meta data has been sent.");
				return responseMessage;
			}
		} else if (serverStatus.equals(ServerStatusType.LOCKED_WRITE)) {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_WRITE_LOCK, key, value);
			logger.info("Sever is locked for writing.");
			return responseMessage;
		} else {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_STOPPED, key, value);
			logger.info("Server is stopped. Therefore you cannnot communicate with the server.");
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
