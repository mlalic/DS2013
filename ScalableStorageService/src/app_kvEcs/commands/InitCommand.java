package app_kvEcs.commands;

import org.apache.log4j.Logger;

import app_kvEcs.ECS;


public class InitCommand extends Command {
    
    private static Logger logger = Logger.getRootLogger();
    
    public InitCommand(Context context, String[] parameters){
        super(context, parameters);
    }
    
    @Override
    public boolean execute() {
        //Instantiate a new ECS with Config File as Parameter
        ECS ecs = new ECS(parameters[0]);
        if(ecs.buildInitialMetadata()){
            context.setSession(ecs);
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public boolean isValid() {
        if(parameters.length==1)
            return true;
        else
            return false;
    }

}
