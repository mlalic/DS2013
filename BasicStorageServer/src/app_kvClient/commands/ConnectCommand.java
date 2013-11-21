package app_kvClient.commands;

import java.io.IOException;
import java.net.UnknownHostException;

import client.KVCommInterface;
import client.KVStore;

public class ConnectCommand extends Command {
    public ConnectCommand(Context context, String[] parameters, String line) {
        super(context, parameters, line);
    }

    @Override
    public boolean execute() {
        KVCommInterface session = context.getSession();
        if (session != null) {
            // There is an already active session.
            // Need to ask the user to disconnect first.
            writeResponse("A session is already active. Please disconnect first.");
            return true;
        }
        // TODO Create a KVStore instance...
        session = new KVStore(parameters[0], Integer.parseInt(parameters[1]));
        try{
            session.connect();
            //Write Appropriate Response
            context.setSession(session);
            writeResponse("Connection Established");
            return true;
        }
        catch(UnknownHostException uHEx){
            //Log
            writeResponse("Unknown Host "+parameters[0]+":"+parameters[1]);
            return false;
        }
        catch(IOException iOEx){
            //Log
            return false;
        }
        catch(Exception ex){
            ///Log
            return false;
        }
    }

    @Override
    public boolean isValid() {
        if (parameters == null || parameters.length != 2) {
            return false;
        }
        // Check that a port is a number
        try {
            Integer.parseInt(parameters[1]);
        } catch (NumberFormatException exc) {
            return false;
        }

        return true;
    }
}
