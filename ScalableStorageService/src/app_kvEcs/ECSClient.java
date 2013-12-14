package app_kvEcs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import app_kvEcs.commands.CommandFactory;
import app_kvEcs.commands.Command;

public class ECSClient {
    private final static String PROMPT = "ECS> ";
    private static Logger logger =  Logger.getRootLogger();
    
    public static void main(String[] args){
        try {
            new LogSetup("logs/client/client.log", Level.ALL);
        } catch (IOException e1) {
            logger.error("Error! Unable to initialize logger!");
            e1.printStackTrace();
            System.exit(1);
        }
        CommandFactory factory = new CommandFactory();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        
        //Set Initial Context Before Offering Prompt
        Command initCommand = factory.createCommand("init "+args[0]);
        initCommand.execute();
        
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
