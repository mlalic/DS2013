package app_kvServer.commands;

import org.apache.log4j.Logger;

import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import app_kvServer.KVServer.ServerStatusType;
import app_kvServer.ServerContext;

public class UnlockWriteServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public UnlockWriteServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		serverContext.setServerStatus(ServerStatusType.STARTED);
		responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
		logger.info("Server is unlocked for writing.");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverContext.getServerStatus().equals(ServerStatusType.LOCKED_WRITE)) {
			return true;
		}
		return false;
	}

}
