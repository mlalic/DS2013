package app_kvServer.commands;

import app_kvServer.ServerContext;

import common.messages.KVMessage;

public class ServerCommandFactory {
	
	private ServerContext serverContext;
	
	public ServerCommandFactory(final ServerContext serverContext) {
		this.serverContext = serverContext;
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
			serverCommand = new GetServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.PUT.equals(status)) {
			serverCommand = new PutServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.INIT_SEREVER.equals(status)) {
			serverCommand = new InitServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.START_SERVER.equals(status)) {
			serverCommand = new StartServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.STOP_SERVER.equals(status)) {
			serverCommand = new StopServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.SHUT_DOWN.equals(status)) {
			serverCommand = new ShutDownServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.LOCK_WRITE.equals(status)) {
			serverCommand = new LockWriteServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.UNLOCK_WRITE.equals(status)) {
			serverCommand = new UnlockWriteServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.UPDATE_METADATA.equals(status)) {
			serverCommand = new UpdateMetaDataCommandMessage(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		}
		else if (KVMessage.StatusType.MOVE_DATA.equals(status)) {
			serverCommand = new MoveDataServerCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
		} else if (KVMessage.StatusType.BULK_DATA_MOVE.equals(status)) {
			serverCommand = new BulkDataMoveCommand(requestMessage.getKey(), requestMessage.getValue(), serverContext);
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
