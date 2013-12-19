package app_kvEcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import app_kvEcs.commands.CommandFactory;
import app_kvEcs.commands.Command;
import app_kvEcs.commands.Context;

public class ECSClient {
    private final static String PROMPT = "ECS> ";
    private static Logger logger =  Logger.getRootLogger();
    
    public static void main(String[] args){
    	if (args.length != 1) {
    		System.out.println("Invalid number of arguments!");
    		System.exit(1);
    	}
        try {
            new LogSetup("logs/ecs/ecs.log", Level.ALL);
        } catch (IOException e1) {
            logger.error("Error! Unable to initialize logger!");
            e1.printStackTrace();
            System.exit(1);
        }

        // Setup the CLI app's context...
        Context context = new Context();
        context.setOutputStream(System.out);        
        // The ECS instance for this process...
        final String configFilePath = args[0];
        ECS ecs = new ECS(configFilePath);
        if (ecs.buildInitialMetadata()) {
            context.setECS(ecs);
        } else {
        	System.out.println("Error setting up the initial metadata. Does your config file contain syntax errors?");
        	System.exit(1);
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;

        // Create a factory instance with the CLI app's context
        CommandFactory factory = new CommandFactory(context);
        while (true) {
            System.out.print(PROMPT);
            try {
                line = reader.readLine();
                logger.info("You entered this text: " + line.toString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                logger.error("Exception in main method: " + "\n");
                logger.error(e);
            }
            if (line == null) {
                // EOF
                return;
            }
            Command command = factory.createCommand(line);
            if (command == null) {
                System.out.println("Invalid command.");
                logger.info("Unfortunately your command is invalid.");
                continue;
            } else {
                command.execute();            
                logger.info("Entered command has been executed.");
            }
        }
    }
}
