package app_kvServer.commands;

import org.apache.log4j.Logger;

import common.messages.KVMessage;

public class MoveDataServerCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	private String nodeName;
	
	public MoveDataServerCommand(String key, String value, String nodeName) {
		super(key, value);
		this.nodeName = nodeName;
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
