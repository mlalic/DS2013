package app_kvEcs.commands;

import java.util.HashSet;

import org.apache.log4j.Logger;

import app_kvEcs.communication.TcpNodeCommunicator;
import app_kvEcs.communication.NodeCommunicator;
import app_kvEcs.communication.kvMessageBuilder;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.metadata.MetaData;
import common.metadata.ServerNode;

public class AddNodeCommand extends Command {

	private static Logger logger = Logger.getRootLogger();
	
    public AddNodeCommand(Context context, String[] parameters) {
        super(context, parameters);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        KVMessage response;
        HashSet<ServerNode> availableNodes = context.getECS().getAvailableNodes();
        if(availableNodes.isEmpty()){
            writeError("There are no free nodes available");
            return false;
        }
        else{
            //Bring Up New Node
            MetaData metaData = context.getECS().getMetaData();
            ServerNode newNode = availableNodes.iterator().next();
            availableNodes.remove(newNode);
            context.getECS().setAvailableNodes(availableNodes);
            SSHCommunication.SSHDeploy(newNode.getIpAddress(),
                    Integer.toString(newNode.getPort()),
                    newNode.getName());
            NodeCommunicator connection = new TcpNodeCommunicator(newNode);
            try {
                connection.connect();
                context.getConnections().add(connection);
                metaData.addServer(newNode);
                context.getECS().setMetaData(metaData);
                KVMessage updateResponse = connection.sendMessage(
                        kvMessageBuilder.buildUpdateMetaDataMessage(metaData));
                if (updateResponse == null || updateResponse.getStatus() != StatusType.ACK) {
                	logger.error("Invalid response from server -- new node not started.");
                	throw new Exception("New node not started - unable to initialize it.");
                }
                KVMessage startResponse = connection.sendMessage(
                        kvMessageBuilder.buildStartMessage());
                if (startResponse == null || startResponse.getStatus() != StatusType.ACK) {
                	logger.error("Invalid response from server -- new node not started.");
                	throw new Exception("New node not started - unable to start it.");
                }
                
                //Ask Successor to Move Data
                ServerNode successor = metaData.getSuccessor(newNode);
                NodeCommunicator suConnection = context.getNodeConnection(successor);
                KVMessage lockResponse = suConnection.sendMessage(
                        kvMessageBuilder.buildWriteLockMessage());
                if (lockResponse == null || lockResponse.getStatus() != StatusType.ACK) {
                	logger.error("Invalid response from server -- unable to lock the successor of the new node.");
                	// TODO Maybe halt the new node's server process before returning and return it to the available pool...
                	throw new Exception("New node not added to the ring -- unable to lock the successor of the new node");
                }
                
                suConnection.sendMessage(
                        kvMessageBuilder.buildUpdateMetaDataMessage(context.getECS().getMetaData()));
                
                response = suConnection.sendMessage(
                        kvMessageBuilder.buildMoveMessage(newNode));
                if (response.getStatus().equals(KVMessage.StatusType.ACK)){
                	// Only when the data is successfully moved is the new node to be
                	// considered a part of the ring and thus the new metadata broadcasted
                	// to all nodes in the ring.
                    for (NodeCommunicator con : context.getConnections()){
                        con.sendMessage(kvMessageBuilder.buildUpdateMetaDataMessage(
                        		context.getECS().getMetaData()));
                    }
                }
                // The successor needs to be unlocked for writes no matter if the data
                // was successfully moved or not.
                // When data isn't moved, the ring needs to stay unchanged, which means the
                // successor needs to keep serving requests as before.
                suConnection.sendMessage(
                        kvMessageBuilder.buildReleaseLockMessage());

                return true;
            } catch (Exception e) {            	
                return false;
            }
        }
    }
    
    @Override
    public boolean isValid() {
        if(parameters.length==0){
            return true;
        }
        return false;
    }
}
