package app_kvServer.commands;

import org.apache.log4j.Logger;

import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import app_kvServer.KVServer.ServerStatusType;

public class UnlockWriteServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	private ServerStatusType serverStatus;
	
	public UnlockWriteServerCommand(String key, String value, final ServerStatusType serverStatus) {
		super(key, value);
		this.serverStatus = serverStatus;
	}

	@Override
	public KVMessage execute() {
		serverStatus = ServerStatusType.STARTED;
		responseMessage = new KVMessageImpl(KVMessage.StatusType.UNLOCK_WRITE_ACK, key, value);
		logger.info("Server is unlocked for writing.");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverStatus.equals(ServerStatusType.LOCKED_WRITE)) {
			return true;
		}
		return false;
	}

}
