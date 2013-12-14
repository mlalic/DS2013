package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import app_kvServer.ServerContext;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class ShutDownServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public ShutDownServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		serverContext.setServerStatus(ServerStatusType.IDLE);
		responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
		logger.info("Server is successesfully shuted down. Now server is in the idle state (waiting to be added in ring again).");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverContext.getServerStatus() != ServerStatusType.IDLE) {
			return true;
		}
		return false;
	}

}
