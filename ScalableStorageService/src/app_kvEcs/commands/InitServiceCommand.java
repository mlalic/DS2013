package app_kvEcs.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import app_kvEcs.communication.NodeDeployer;
import app_kvEcs.communication.SshNodeDeployer;
import app_kvEcs.communication.kvMessageBuilder;
import common.communication.NodeCommunicator;
import common.communication.TcpNodeCommunicator;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.metadata.MetaData;
import common.metadata.ServerNode;

public class InitServiceCommand extends Command{
    
    private static final int INITIALIZATION_DELAY = 1000;  // 1 second
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
        MetaData metaData = context.getECS().getMetaData();
        
        HashSet<ServerNode> availableNodes = context.getECS().getAvailableNodes();
        Iterator<ServerNode> serverIterator = availableNodes.iterator();
        List<ServerNode> toRemove = new LinkedList<ServerNode>(); 
        for (int i=0; i<serverCount; i++){
            ServerNode server = serverIterator.next();
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
        
        try {
            // SSHDeploy to bring up processes
        	if (!startAllNodes(metaData)) {
        		return false;
        	}
            
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

	private boolean startAllNodes(MetaData metaData) throws InterruptedException {
		writeResponse("Starting up server nodes...");
		NodeDeployer sshCommunicator = context.getDeployer();

		for (ServerNode node: metaData.getServers()) {
			writeResponse(String.format(" - %s at %s:%s", node.getName(), node.getIpAddress(), node.getPort()));
			if (!sshCommunicator.deploy(node)) {
				writeError("Unable to start up node " + node.getName() + ". Error initializing the service!");
				return false;
			}
		}

		writeResponse("Waiting for them to spin up...");
		Thread.sleep(INITIALIZATION_DELAY);
		return true;
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
