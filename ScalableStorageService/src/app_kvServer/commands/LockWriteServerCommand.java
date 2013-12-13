package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class LockWriteServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	private ServerStatusType serverStatus;
	
	public LockWriteServerCommand(String key, String value, final ServerStatusType serverStatus) {
		super(key, value);
		this.serverStatus = serverStatus;
	}

	@Override
	public KVMessage execute() {
		serverStatus = ServerStatusType.LOCKED_WRITE;
		responseMessage = new KVMessageImpl(KVMessage.StatusType.LOCK_WRITE_ACK, key, value);
		logger.info("Server is locked for writing.");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverStatus.equals(ServerStatusType.STARTED)) {
			return true;
		}
		return false;
	}

}
