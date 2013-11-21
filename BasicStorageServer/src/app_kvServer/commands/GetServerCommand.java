package app_kvServer.commands;

import java.util.concurrent.ConcurrentHashMap;

import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class GetServerCommand extends ServerCommand {

	public GetServerCommand(String key, String value, final ConcurrentHashMap<String, String> serverStorage) {
		super(key, value, serverStorage);
	}

	/**
	 * Get the message from the serverStorage with the given name
	 * If we find a message with the given key, then status is GET_SUCCESS
	 * If we don't find a message with the given name then staus is GET_ERROR 
	 */
	@Override
	public KVMessage execute() {
		String value = serverStorage.get(key);
		if (value == null) {
			responseMessage = new KVMessageImpl(
					KVMessage.StatusType.GET_ERROR,
					key,
					"Key '" + key + "' not found");
			return responseMessage;
		}
		else {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.GET_SUCCESS, key, value);
			return responseMessage;
		}
	}

}
