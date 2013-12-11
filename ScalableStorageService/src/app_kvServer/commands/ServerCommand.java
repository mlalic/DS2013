package app_kvServer.commands;

import java.util.concurrent.ConcurrentHashMap;

import common.messages.*;

public abstract class ServerCommand {
	
	protected String key;
	protected String value;
	protected ConcurrentHashMap<String, String> serverStorage;
	protected KVMessage responseMessage = null;
	
	public ServerCommand(String key, String value, final ConcurrentHashMap<String, String> serverStorage) {
		this.key = key;
		this.value = value;
		this.serverStorage = serverStorage;
	}
	
	public abstract KVMessage execute();
	
	public boolean isValid() {
		if (key == null || value == null) {
			return false;
		}
		return true;
	}
}
