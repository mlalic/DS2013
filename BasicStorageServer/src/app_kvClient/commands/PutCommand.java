package app_kvClient.commands;

import java.io.IOException;

import client.KVCommInterface;
import client.NotConnectedException;

import common.messages.KVMessage;

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
            writeResponse(response.getValue());
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
