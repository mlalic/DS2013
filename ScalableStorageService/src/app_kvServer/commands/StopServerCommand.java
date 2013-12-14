package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import app_kvServer.ServerContext;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class StopServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public StopServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		serverContext.setServerStatus(ServerStatusType.STOPPED);
		responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
		logger.info("Server is successfully stopped.");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverContext.getServerStatus().equals(ServerStatusType.STARTED) || serverContext.getServerStatus().equals(ServerStatusType.LOCKED_WRITE)) {
			return true;
		}
		return false;
	}

}
