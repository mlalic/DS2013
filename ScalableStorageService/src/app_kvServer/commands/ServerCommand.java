package app_kvServer.commands;

import common.messages.*;

public abstract class ServerCommand {
	
	protected String key;
	protected String value;
	protected KVMessage responseMessage = null;
	
	public ServerCommand(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public abstract KVMessage execute();
	
	public abstract boolean isValid();
}
