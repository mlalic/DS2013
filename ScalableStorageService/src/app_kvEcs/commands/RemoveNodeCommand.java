package app_kvEcs.commands;

import java.util.Collection;
import java.util.HashSet;

import app_kvEcs.communication.kvMessageBuilder;
import common.communication.NodeCommunicator;
import common.messages.KVMessage;
import common.metadata.MetaData;
import common.metadata.ServerNode;

public class RemoveNodeCommand extends Command {

    public RemoveNodeCommand(Context context, String[] parameters) {
        super(context, parameters);
    }

    @Override
    public boolean execute() {
    	// TODO Precondition checks: The current ring must not be empty when the command is invoked...
        HashSet<ServerNode> availableNodes = context.getECS().getAvailableNodes();
        MetaData metaData = context.getECS().getMetaData();
        KVMessage response;
        // Get a random node that is currently in the ring.
        // TODO Make it truly random (this is why the call is factored out to a method).
        ServerNode nodeToRemove = getRandomNode(metaData.getServers());
        // The node that is the successor of the node to be removed (in the current ring)
        ServerNode successorNode = metaData.getSuccessor(nodeToRemove);
    	// Put the node back to the available pool
        availableNodes.add(nodeToRemove);
        // But remove it from the current ring
        metaData.removeServer(nodeToRemove);
        context.getECS().setMetaData(metaData);

        // Set up the connections to the two affected nodes
        NodeCommunicator removeCon = context.getNodeConnection(nodeToRemove);
     
        // Shuffle the data around from the node to be removed to its successor in the ring 
        
        // Lock the node which is to be removed for further writes
        response = removeCon.sendMessage(
                kvMessageBuilder.buildWriteLockMessage());
        
        // MOVE MESSAGE
        response = removeCon.sendMessage(kvMessageBuilder.buildMoveMessage(successorNode));
        if (response == null) {
        	// No response received -> move unsuccessful
        	writeError("Unable to move data from the server - no acknowledge received");
        	return false;
        }

        if (response.getStatus().equals(KVMessage.StatusType.ACK)){
        	// When the ECS gets the acknowledgment that all data has been moved, it is
        	// safe to "commit" the changes, i.e. make them permanent by propagating
        	// the new metadata without the removed node to each leftover node.
            for (NodeCommunicator con : context.getConnections()){
                con.sendMessage(
                        kvMessageBuilder.buildUpdateMetaDataMessage(
                                context.getECS().getMetaData()));
            }
            
            // The node is to be shutdown if the move of data is successful
            removeCon.sendMessage(
                    kvMessageBuilder.buildShutdownMessage());
            context.getConnections().remove(removeCon);

            return true;
        } else {
        	// When the data is not moved, try to "rollback" the state of the ring
        	// The only change which needs to be undone is to make the node which
        	// should have been removed available for write operations once again.
        	removeCon.sendMessage(kvMessageBuilder.buildReleaseLockMessage());
        	return false;
        }
    }

    /**
     * Private helper method which returns a random element from the given collection.
     * TODO Should be truly random...
     * @param servers
     * @return
     */
    private ServerNode getRandomNode(Collection<ServerNode> servers) {
    	return servers.iterator().next();
	}

    @Override
    public boolean isValid() {
        if (parameters.length==0){
            return true;
        }
        else{
            return false;
        }
    }
    
}
