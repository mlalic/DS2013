package app_kvEcs.commands;

import java.util.Arrays;

public class CommandFactory {
    private Context context;
    public CommandFactory(){
        context = new Context();
        context.setOutputStream(System.out);
    }
    /**
     * Creates a new Command instance based on a given string line.
     * @param line The text representation of the command.
     * @return A Command instance for the given command line
     *         null if the given command is invalid
     */
    public Command createCommand(String line) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 0) {
            return null;
        }
        final String commandName = parts[0];
        // Decide which concrete class to instantiate
        Command command = null;
        String[] parameters = Arrays.copyOfRange(parts, 1, parts.length);
        if (commandName.equals("init")) {
            command = new InitCommand(context, parameters);
        }
        else if (commandName.equals("quit")){
            command = new QuitCommand(context, parameters);
        }
        else if (commandName.equalsIgnoreCase("initService")){
            command = new InitServiceCommand(context, parameters);
        }
        // No concrete class matches the command name
        if (command == null) {
            return null;
        }

        // Check if the parameters given to the command are valid
        if (!command.isValid()) {
            return null;
        }

        // Everything is okay.
        return command;
    }

}
