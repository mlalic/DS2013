package app_kvServer.commands;


import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import app_kvServer.ServerContext;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class StartServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	private static Set<ServerStatusType> validStatusTypes = new HashSet<ServerStatusType>();

	static {
		// If the server is not in one of the give states, the command is invalid
		validStatusTypes.add(ServerStatusType.STOPPED);
		validStatusTypes.add(ServerStatusType.LOCKED_WRITE);
		validStatusTypes.add(ServerStatusType.IDLE);
	}
	
	public StartServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		serverContext.setServerStatus(ServerStatusType.STARTED);
		responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
		logger.info("Server is successfully started.");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		return validStatusTypes.contains(serverContext.getServerStatus());
	}

}
