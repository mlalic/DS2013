package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.ServerContext;
import common.messages.KVMessage;

public class MoveDataServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
		
	public MoveDataServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		// TODO No idea :)
		return null;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
