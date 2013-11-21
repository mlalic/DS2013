package app_kvServer.commands;

import java.util.concurrent.ConcurrentHashMap;

import common.messages.*;

public class PutServerCommand extends ServerCommand {
	
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
			String oldValue = serverStorage.get(key);
			if (oldValue == null) {
				serverStorage.put(key, value);
				responseMessage = new KVMessageImpl(KVMessage.StatusType.PUT_SUCCESS, key, value);
				return responseMessage;
			}
			else {
				serverStorage.put(key, value);
				responseMessage = new KVMessageImpl(KVMessage.StatusType.PUT_UPDATE, key, value);
				return responseMessage;
			}
		} catch (Exception e) {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.PUT_ERROR, key, e.getMessage());
			return responseMessage;
		}
	}

}
