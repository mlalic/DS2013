package app_kvEcs.commands;

import common.communication.NodeCommunicator;

import app_kvEcs.communication.kvMessageBuilder;

public class StopCommand extends Command {
    
    public StopCommand(Context context, String[] parameters) {
        super(context, parameters);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        for(NodeCommunicator connection : context.getConnections()){
            connection.sendMessage(kvMessageBuilder.buildStopMessage());                                    
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
