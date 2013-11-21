package app_kvClient.commands;

import java.io.IOException;

import client.KVCommInterface;
import client.NotConnectedException;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;

public class PutCommand extends Command {
    public PutCommand(Context context, String[] parameters, String line) {
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
            KVMessage response = session.put(parameters[0], parameters[1]);
            if (response.getStatus() == StatusType.PUT_SUCCESS) {
            	writeResponse(String.format(
            			"Value '%s' for key '%s' successfully saved",
            			response.getValue(),
            			response.getKey()));
            } else if (response.getStatus() == StatusType.PUT_UPDATE) {
            	writeResponse(String.format(
            			"Value for key '%s' successfully updated. The new value is '%s'",
            			response.getKey(),
            			response.getValue()));           	
            } else if (response.getStatus() == StatusType.DELETE_SUCCESS) {
            	writeResponse(String.format(
            			"The key '%s' has been successfully deleted from the KV store",
            			response.getKey()));
            } else if (response.getStatus() == StatusType.DELETE_ERROR) {
            	writeError(String.format(
            			"Error deleting key '%s'. %s",
            			response.getKey(),
            			response.getValue()));
            } else if (response.getStatus() == StatusType.PUT_ERROR) {
            	writeError(String.format(
            			"Error updating value for key '%s'. %s",
            			response.getKey(),
            			response.getValue()));
            } else {
            	// Unknown status code means that there was some other error ...
            	writeError(response.getValue());
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
        return (parameters.length == 2);
    }
}
