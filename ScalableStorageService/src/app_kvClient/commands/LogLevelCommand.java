package app_kvClient.commands;


import java.io.IOException;

import org.apache.log4j.Level;

import logger.LogSetup;

public class LogLevelCommand extends Command {
	private static final String LOG_DIRECTORY = "logs/client/client.log";

	public LogLevelCommand(Context context, String[] parameters, String line) {
		super(context, parameters, line);
	}

	@Override
	public boolean execute() {
		String logLevel = parameters[0];
		if (!LogSetup.isValidLevel(logLevel)) {
			writeError("Invalid log level. Possible levels are: " + LogSetup.getPossibleLogLevels());
			return false;
		}
		try {
			new LogSetup(LOG_DIRECTORY, Level.toLevel(logLevel));
		} catch (IOException e) {
			writeError("Could not change log level.");
			return false;
		}
		writeResponse("New log level set - " + logLevel);
		return true;
	}

	@Override
	public boolean isValid() {
		return parameters != null && parameters.length == 1;
	}

}
