package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class StopServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	private ServerStatusType serverStatus;
	
	public StopServerCommand(String key, String value, final ServerStatusType serverStatus) {
		super(key, value);
		this.serverStatus = serverStatus;
	}

	@Override
	public KVMessage execute() {
		serverStatus = ServerStatusType.STOPPED;
		responseMessage = new KVMessageImpl(KVMessage.StatusType.STOP_ACK, key, value);
		logger.info("Server is successfully stopped.");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverStatus.equals(ServerStatusType.STARTED) || serverStatus.equals(ServerStatusType.LOCKED_WRITE)) {
			return true;
		}
		return false;
	}

}
