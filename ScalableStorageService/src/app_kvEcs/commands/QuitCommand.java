package app_kvEcs.commands;

public class QuitCommand extends Command{

    public QuitCommand(Context context, String[] parameters) {
        super(context, parameters);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        // TODO Auto-generated method stub
        System.exit(0);
        return true;
    }

    @Override
    public boolean isValid() {
        // TODO Auto-generated method stub
        return true;
    }
    

}
