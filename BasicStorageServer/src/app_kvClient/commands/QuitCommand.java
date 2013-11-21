package app_kvClient.commands;

import org.apache.log4j.Logger;

import client.KVCommInterface;

public class QuitCommand extends Command {
	
	private static Logger logger = Logger.getRootLogger();
	
    public QuitCommand(Context context, String[] parameters, String line) {
        super(context, parameters, line);
    }

    @Override
    public boolean execute() {
        // Make sure to close an open connection, if any.
        final KVCommInterface session = context.getSession();
        if (session != null) {
            session.disconnect();
        }
        
        logger.info("Application exit.");
        writeResponse("Application exit!");
        System.exit(0);
        return true;
    }

    @Override
    public boolean isValid() {
        if(parameters != null && parameters.length == 0) {
        	logger.info("Your quit command is valid.");
        	return true;
        }
        return false;
    }
}
