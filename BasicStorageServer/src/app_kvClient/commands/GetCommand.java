package app_kvClient.commands;

import java.io.IOException;

import client.KVCommInterface;
import client.NotConnectedException;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;

public class GetCommand extends Command {
    public GetCommand(Context context, String[] parameters, String line) {
        super(context, parameters, line);
    }

    @Override
    public boolean execute() {
        final KVCommInterface session = context.getSession();
        // If the context does not include an active session with the
        // KV server, throw an error...
        if (session == null) {
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
            writeError("You are not connected. Please connect to a server first.");
        }
        catch(IOException ex){
            //LOG 
        	writeError("Unable to send the request to the server.");
        }
        catch(Exception ex){
            //LOG Unknown Exception
        	writeError(ex.getMessage());
        }
        return true;
    }

    @Override
    public boolean isValid() {
        return (parameters != null);
    }
}
