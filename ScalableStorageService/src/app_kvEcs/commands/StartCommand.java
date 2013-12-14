package app_kvEcs.commands;

import app_kvEcs.communication.kvECSCommInterface;
import app_kvEcs.communication.kvMessageBuilder;

public class StartCommand extends Command {

    public StartCommand(Context context, String[] parameters) {
        super(context, parameters);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        for(kvECSCommInterface connection : context.getConnections()){
            connection.sendMessage(kvMessageBuilder.buildStartMessage());                                    
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
