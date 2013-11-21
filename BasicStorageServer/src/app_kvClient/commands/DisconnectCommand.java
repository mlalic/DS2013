package app_kvClient.commands;

import client.KVCommInterface;

public class DisconnectCommand extends Command {

    public DisconnectCommand(Context context, String[] parameters, String line) {
        super(context, parameters, line);
    }

    @Override
    public boolean execute() {
        final KVCommInterface session = context.getSession();
        if (session == null) {
            writeError("No active connection.");
            return false;
        }
        context.setSession(null);
        session.disconnect();
        writeResponse("Successfully disconnected");
        return true;
    }

    @Override
    public boolean isValid() {
        return parameters != null && parameters.length == 0;
    }
}
