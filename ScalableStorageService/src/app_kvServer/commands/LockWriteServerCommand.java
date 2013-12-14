package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.ServerContext;
import app_kvServer.KVServer.ServerStatusType;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class LockWriteServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public LockWriteServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		serverContext.setServerStatus(ServerStatusType.LOCKED_WRITE);
		responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
		logger.info("Server is locked for writing.");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverContext.getServerStatus().equals(ServerStatusType.STARTED)) {
			return true;
		}
		return false;
	}

}
