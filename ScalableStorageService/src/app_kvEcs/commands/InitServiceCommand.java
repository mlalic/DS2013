package app_kvEcs.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import app_kvEcs.communication.SSHCommunication;
import app_kvEcs.communication.TcpNodeCommunicator;
import app_kvEcs.communication.NodeCommunicator;
import app_kvEcs.communication.kvMessageBuilder;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.metadata.MetaData;
import common.metadata.ServerNode;

public class InitServiceCommand extends Command{
    
    private int serverCount; 
	private static Logger logger = Logger.getRootLogger();	
    
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
    	// TODO Code smell ...
    	//      This whole method needs to be refactored into the ECS class...
    	//      The command then just invokes the method on the ECS object it gets from the context.
        serverCount = Integer.parseInt(parameters[0]);
        // TODO Check that the given serverCount is <= total server count in the config file...
        ArrayList<ServerNode> servers = new ArrayList<ServerNode>();
        ArrayList<NodeCommunicator> connections = new ArrayList<NodeCommunicator>();
        String addresses = "";
        String ports = "";
        String names = "";
        MetaData metaData = context.getECS().getMetaData();
        
        HashSet<ServerNode> availableNodes = context.getECS().getAvailableNodes();
        Iterator<ServerNode> serverIterator = availableNodes.iterator();
        List<ServerNode> toRemove = new LinkedList<ServerNode>(); 
        for (int i=0; i<serverCount; i++){
            ServerNode server = serverIterator.next();
            String address = server.getIpAddress();
            String port = Integer.toString(server.getPort());
            String name = server.getName();
            addresses = addresses + address + ",";
            ports = ports + port +",";
            names = names + name +",";
            servers.add(server);
            metaData.addServer(server);
            // Keep track which nodes have been added in this run in order
            // to remove them from the list of available nodes later on.
            toRemove.add(server);
        }
        // Remove the nodes which were added to the ring from the list
        // of available nodes...
        for (ServerNode server: toRemove) {
            availableNodes.remove(server);
        }
        // TODO This is probably unnecessary, isn't it?
        context.getECS().setAvailableNodes(availableNodes);
        context.getECS().setMetaData(metaData);
        
        //Remove trailing ','s
        addresses = addresses.substring(0, addresses.length()-1);
        ports = ports.substring(0,ports.length()-1);
        names = ports.substring(0,names.length()-1);
        try{
            //SSHDeploy to bring up processes
            SSHCommunication.SSHDeploy(addresses, ports, names);
            
            //Connections to the spawned processes
            for( ServerNode s : servers ){
                NodeCommunicator connection = new TcpNodeCommunicator(s);
                connection.connect();
                connections.add(connection);
                KVMessage response = connection.sendMessage(
                		kvMessageBuilder.buildUpdateMetaDataMessage(
                                context.getECS().getMetaData()));
                if (response == null) {
                	throw new Exception("Could not initialize server " + s.getName() + ". No response received.");
                }
                logger.info(String.format(
                		"Response from server node %s: %s",
                		s.getName(),
                		response.getStatus().toString()));
                if (response.getStatus() != StatusType.ACK) {
                	throw new Exception("Could not initialize server " + s.getName() + ". Invalid response received.");
                }
            }            
            context.setConnections(connections);
            return true;
        }
        catch (Exception e){
        	writeError(e.getMessage());
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
