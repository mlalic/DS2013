package app_kvEcs.commands;

import java.util.HashSet;

import app_kvEcs.communication.kvECSCommInterface;
import app_kvEcs.communication.kvMessageBuilder;

import common.messages.KVMessage;
import common.metadata.MetaData;
import common.metadata.ServerNode;

public class RemoveNodeCommand extends Command {

    public RemoveNodeCommand(Context context, String[] parameters) {
        super(context, parameters);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        HashSet<ServerNode> availableNodes = context.getECS().getAvailableNodes();
        MetaData metaData = context.getECS().getMetaData();
        KVMessage response;
        ServerNode nodeToRemove = availableNodes.iterator().next();
        try{
            //INFORM NODE AND SUCCESSOR
            availableNodes.remove(nodeToRemove);
            metaData.removeServer(nodeToRemove);
            context.getECS().setMetaData(metaData);
            kvECSCommInterface removeCon = getNodeConnection(nodeToRemove);
            response = removeCon.sendMessage(
                    kvMessageBuilder.buildWriteLockMessage());
            kvECSCommInterface suConn = getNodeConnection(
                    context.getECS().getMetaData().getPredecessor(//SUCCESSOR?
                            nodeToRemove.getHash()));
            suConn.sendMessage(
                    kvMessageBuilder.buildUpdateMetaDataMessage(
                            context.getECS().getMetaData(),
                            suConn.getHostName()));
            
            //MOVE MESSAGE
            response = removeCon.sendMessage(
                    kvMessageBuilder.buildMoveMessage());
            if (response.getStatus().equals(KVMessage.StatusType.ACK)){
                //Broadcast MetaDataUpdate
                for (kvECSCommInterface con : context.getConnections()){
                    con.sendMessage(
                            kvMessageBuilder.buildUpdateMetaDataMessage(
                                    context.getECS().getMetaData(),
                                    suConn.getHostName()));
                }
                
                //Shutdown Node
                removeCon.sendMessage(
                        kvMessageBuilder.buildShutdownMessage());
                context.getConnections().remove(removeCon);
                return true;
            }
            return false;
        }
        catch (Exception e){
            writeResponse("An Exception Occurred");
            e.printStackTrace();
            return false;
        }
    }

    public kvECSCommInterface getNodeConnection(ServerNode node){
        for ( kvECSCommInterface connection : context.getConnections()){
            if (connection.getHostName().equals(node.getName())){
                return connection;
            }
        }
        return null;
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
