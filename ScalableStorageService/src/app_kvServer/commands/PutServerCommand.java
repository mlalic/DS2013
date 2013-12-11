package app_kvServer.commands;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import common.messages.*;

public class PutServerCommand extends ServerCommand {
	
	private static Logger logger = Logger.getRootLogger();
	
	public PutServerCommand(String key, String value, final ConcurrentHashMap<String, String> serverStorage) {
		super(key, value, serverStorage);
	}

	/**
	 *  Put the message with the given key in the serverStorage
	 *  If it is first time to put the message with the given key then status is PUT_SUCCESS
	 *  If there is a message with the given key, then status is PUT_UPDATE
	 *  If there is some problem with getting and putting values then status of response is PUT_ERROR
	 */
	@Override
	public KVMessage execute() {
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

	@Override
	public boolean isValid() {
		if (key == null) {
			return false;
		}
		return true;
	}

}
