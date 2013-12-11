package app_kvClient.commands;

import org.apache.log4j.Logger;

public class HelpCommand extends Command {
	
	private static Logger logger = Logger.getRootLogger();
	
	public HelpCommand(Context context, String[] parameters, String line) {
		super(context, parameters, line);
	}

	@Override
	public boolean execute() {
		if (parameters.length == 0) {
			writeResponse(
					"You can choose one of these commands: \n" +
					" help connect \n" +
					" help disconnect \n" +
					" help put \n" +
					" help get \n" +
					" help quit \n");
			return true;
		} 
		else if ("connect".equals(parameters[0])) {
			writeResponse("With the connect command you can try to establish network with the server. \n"
					+ "After calling 'connect' command you should type connection address and port of the server. \n"
					+ "Exapmle: connect <address> <port> \n");
			return true;
		} 
		else if ("disconnect".equals(parameters[0])) {
			writeResponse("After calling the 'disconnect' command you will be disconnected from the server. \n"
					+ "Example: disconnect \n");
			return true;
		}
		else if ("put".equals(parameters[0])) {
			writeResponse("After calling 'put' command you should enter the key and the value. \n"
					+ "Example: send <key> <value> \n");
			return true;
		}
		else if ("get".equals(parameters[0])){
		    writeResponse("After calling the 'get' command you should enter the key whose value you want"
		            + "Example: get <key>");
		}
		else if ("quit".equals(parameters[0])) {
			writeResponse("After calling 'quit' command you program will be closed. \n"
					+ "Example: quit \n");
			return true;
		}
		return false;
	}

	@Override
	public boolean isValid() {
		if (parameters.length == 0) {
			logger.info("Your help command is valid.");
			return true;
		} else if ("connect".equals(parameters[0])
				|| "disconnect".equals(parameters[0])
				|| "put".equals(parameters[0])
				|| "quit".equals(parameters[0])
				|| "get".equals(parameters[0])) {
			
			logger.info("Your help command is valid.");
			return true;
		}
		else {
			return false;
		}
	}
}
