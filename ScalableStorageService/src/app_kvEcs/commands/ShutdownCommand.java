package app_kvEcs.commands;

import app_kvEcs.communication.kvECSCommInterface;
import app_kvEcs.communication.kvMessageBuilder;

public class ShutdownCommand extends Command {
    public ShutdownCommand(Context context, String[] parameters) {
        super(context, parameters);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        for(kvECSCommInterface connection : context.getConnections()){
            connection.sendMessage(kvMessageBuilder.buildShutdownMessage());                                    
        }
        return true;
    }

    @Override
    public boolean isValid() {
        if(parameters.length!=0){
            return false;
        }
        else{
            return true;
        }
    }


}
