package app_kvClient.commands;

import java.util.Arrays;

public class CommandFactory {
    private Context context;
    public CommandFactory() {
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
        if (commandName.equals("quit")) {
            command = new QuitCommand(context, parameters, line);
        } else if (commandName.equals("connect")) {
            command = new ConnectCommand(context, parameters, line);
        } else if (commandName.equals("disconnect")) {
            command = new DisconnectCommand(context, parameters, line);
        }else if(commandName.equals("help")) {
            command = new HelpCommand(context, parameters, line);
        }else if(commandName.equals("put")) {
            command = new PutCommand(context, parameters, line);
        }else if(commandName.equals("get")) {
            command = new GetCommand(context, parameters, line);
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
