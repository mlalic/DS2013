package app_kvServer.commands;

import java.util.concurrent.ConcurrentHashMap;

import javax.print.attribute.standard.Severity;

import common.messages.*;

public class ServerCommandFactory {
	
	private ConcurrentHashMap<String, String> serverStorage;
	
	public ServerCommandFactory(final ConcurrentHashMap<String, String> serverStorage) {
		this.serverStorage = serverStorage;
	}
	
	/**
	 * Creating server command depending on the requestMessage status
	 * If the status of the requestMessage is PUT, then we have to make a PutServerCommand
	 * At the other side if the status is GET, then we have to create a GetServerCommand
	 * Otherwise if there is some unknown status that we cannot recognize we will return null 
	 * @param requestMessage - type: KVMessage consists of status, key and value
	 * @return severCommand - type: ServerCommand
	 */
	public ServerCommand createServerCommand(KVMessage requestMessage) {
		KVMessage.StatusType status = requestMessage.getStatus();
		ServerCommand serverCommand = null;
		if (KVMessage.StatusType.GET.equals(status)) {
			serverCommand = new GetServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverStorage);
		}
		else if (KVMessage.StatusType.PUT.equals(status)) {
			serverCommand = new PutServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverStorage);
		}
		
		if (serverCommand == null) {
			return null;
		}
		
		if (!serverCommand.isValid()) {
			return null;
		}
		
		return serverCommand;
	}
	
}
