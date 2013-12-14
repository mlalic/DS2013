package app_kvEcs.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import app_kvEcs.communication.SSHCommunication;
import app_kvEcs.communication.kvECSComm;
import app_kvEcs.communication.kvECSCommInterface;
import app_kvEcs.communication.kvMessageBuilder;
import common.metadata.ServerNode;

public class InitServiceCommand extends Command{
    
    private int serverCount; 
    
    public InitServiceCommand(Context context, String[] parameters) {
        super(context, parameters);
    }

    /***
     * This method performs 2 tasks
     * Sends ssh invocations to remote/local servers 
     * to start their server processes.
     * It then establishes connections to those processes and 
     * sends them the initial metadata
     * Note - The Connections created here are persistent for
     * the duration ECS is up.
     */
    public boolean execute() {
        serverCount = Integer.parseInt(parameters[0]);
        ArrayList<ServerNode> servers = new ArrayList<ServerNode>();
        ArrayList<kvECSCommInterface> connections = new ArrayList<kvECSCommInterface>();
        String addresses = "";
        String ports = "";
        Iterator<ServerNode> serverIterator = context.getSession().getNodes().iterator();
        for (int i=0; i<serverCount; i++){
            ServerNode server = serverIterator.next();
            String address = server.getIpAddress();
            String port = Integer.toString(server.getPort());
            addresses = addresses + address + ",";
            ports = ports + port +",";
            servers.add(server);
        }
        //Remove trailing ','s
        addresses = addresses.substring(0, addresses.length()-1);
        ports = ports.substring(0,ports.length()-1);
        try{
            //SSHDeploy to bring up processes
            SSHCommunication.SSHDeploy(addresses, ports);
            
            //Connections to the spawned processes
            for( ServerNode s : servers){
                kvECSCommInterface connection = new kvECSComm();
                connection.connect(s.getIpAddress(), s.getPort());
                connections.add(connection);
                connection.sendMessage(kvMessageBuilder.
                        buildUpdateMetaDataMessage(
                                context.getSession().getMetaData(),
                        s.getName()));
            }            
            context.setConnections(connections);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean isValid() {
        if (parameters.length == 1){
            try{
                Integer.parseInt(parameters[0]);
            }
            catch (NumberFormatException e){
                return false;
            }
            return true;
        }
        else{
            return false;
        }
    }

}
