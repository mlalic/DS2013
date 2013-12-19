package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.ServerContext;

import common.messages.KVMessage;

public class ShutDownServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public ShutDownServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		logger.info("Server received a SHUTDOWN COMMAND! Stopping now.");
		System.exit(0);
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
