package app_kvClient;

import app_kvClient.commands.Command;
import app_kvClient.commands.CommandFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

//import org.apache.log4j.Logger;

//import logging.MyLogger;

public class KVClient {
    private final static String PROMPT = "BasicStorageServer> ";
    

    public static void main(String[] args) {
        
        //MyLogger mylog = MyLogger.getInstance();
        //mylog.createLogger(Application.class);
        //Logger log = mylog.getLogger();
        
        //log.info("Program is started.");
        
        CommandFactory factory = new CommandFactory();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String line = null;
        while (true) {
            System.out.print(PROMPT);
            try {
                line = reader.readLine();
                //log.info("You entered this text: " + line.toString());
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                //log.error("Exception in the class: " + log.getName());
                //log.error(e.getStackTrace());
            }
            if (line == null) {
                // EOF
                return;
            }
            Command command = factory.createCommand(line);
            if (command == null) {
                System.out.println("Invalid command.");
                
                //log.info("Unfortunately your command is invalid.");
                continue;
            } else {
                command.execute();
                
                //log.info("Entered command has been executed.");
            }
        }
    }
}
