package app_kvServer.commands;

import java.awt.TrayIcon.MessageType;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;

public class ShutDownServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	private ServerStatusType serverStatus;
	
	public ShutDownServerCommand(String key, String value, final ServerStatusType serverStatus) {
		super(key, value);
		this.serverStatus = serverStatus;
	}

	@Override
	public KVMessage execute() {
		serverStatus = ServerStatusType.IDLE;
		responseMessage = new KVMessageImpl(KVMessage.StatusType.SHUT_DOWN_ACK, key, value);
		logger.info("Server is successesfully shuted down. Now server is in the idle state (waiting to be added in ring again).");
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		if (serverStatus != ServerStatusType.IDLE) {
			return true;
		}
		return false;
	}

}
