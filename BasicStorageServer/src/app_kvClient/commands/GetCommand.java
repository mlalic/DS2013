package app_kvClient.commands;

import java.io.IOException;

import client.KVCommInterface;
import client.NotConnectedException;

import common.messages.KVMessage;

public class GetCommand extends Command {
    public GetCommand(Context context, String[] parameters, String line) {
        super(context, parameters, line);
    }

    @Override
    public boolean execute() {
        final KVCommInterface session = context.getSession();        
        try {
            KVMessage response = session.get(parameters[0]);
            writeResponse(response.getValue());
        } 
        catch(NotConnectedException ncEx){
            writeResponse("You are not connected. Please connect to a server first.");
        }
        catch(IOException ex){
            //LOG 
        }
        catch(Exception ex){
            //LOG Unknown Exception
        }
        return true;
    }

    @Override
    public boolean isValid() {
        return (parameters != null);
    }
}
