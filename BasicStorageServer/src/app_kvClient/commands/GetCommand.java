package app_kvClient.commands;

import java.io.IOException;

import org.apache.log4j.Logger;

import client.KVCommInterface;
import client.NotConnectedException;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;

public class GetCommand extends Command {
	
	private static Logger logger = Logger.getRootLogger();
	
    public GetCommand(Context context, String[] parameters, String line) {
        super(context, parameters, line);
    }

    @Override
    public boolean execute() {
        final KVCommInterface session = context.getSession();
        // If the context does not include an active session with the
        // KV server, throw an error...
        if (session == null) {
        	logger.error("No active connection");
        	writeError("No active connection");
        	return false;
        }

        try {
            KVMessage response = session.get(parameters[0]);
            if (response.getStatus() != StatusType.GET_SUCCESS) {
            	// By the protocol, the error message is put in the value
            	// field.
            	writeError(response.getValue());
            } else {
            	writeResponse(String.format(
            			"The value for key '%s' is '%s'",
            			response.getKey(),
            			response.getValue()));
            }
        } 
        catch(NotConnectedException ncEx){
        	logger.error("You are not connected. Please connect to a server first.");
            writeError("You are not connected. Please connect to a server first.");
        }
        catch(IOException ex){
            logger.error("Unable to send the request to the server.");
        	writeError("Unable to send the request to the server.");
        }
        catch(Exception ex){
            //LOG Unknown Exception
        	logger.error(ex.getMessage());
        	writeError(ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean isValid() {
        return (parameters != null);
    }
}
