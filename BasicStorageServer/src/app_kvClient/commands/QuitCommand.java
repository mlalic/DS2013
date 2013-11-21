package app_kvClient.commands;

//import logging.MyLogger;

//import org.apache.log4j.Logger;

import client.KVCommInterface;
//import ui.Application;
//import network.Session;

public class QuitCommand extends Command {
	
	//MyLogger myLog;
	//Logger log;
	
    public QuitCommand(Context context, String[] parameters, String line) {
        super(context, parameters, line);
        
        //myLog = MyLogger.getInstance();
    	//myLog.createLogger(QuitCommand.class);
		//log = myLog.getLogger();
		//log.info("You have made the quit command.");
    }

    @Override
    public boolean execute() {
        // Make sure to close an open connection, if any.
        final KVCommInterface session = context.getSession();
        if (session != null) {
            session.disconnect();
        }
        
        //log.info("Application exit.");
        writeResponse("Application exit!");
        System.exit(0);
        return true;
    }

    @Override
    public boolean isValid() {
        if(parameters != null && parameters.length == 0) {
        	//log.info("Your quit command is valid.");
        	return true;
        }
        return false;
    }
}
