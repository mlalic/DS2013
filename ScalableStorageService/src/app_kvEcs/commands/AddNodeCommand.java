package app_kvEcs.commands;

import java.util.HashSet;

import app_kvEcs.communication.SSHCommunication;
import app_kvEcs.communication.kvECSComm;
import app_kvEcs.communication.kvECSCommInterface;
import app_kvEcs.communication.kvMessageBuilder;

import common.messages.KVMessage;
import common.metadata.MetaData;
import common.metadata.ServerNode;

public class AddNodeCommand extends Command {

    public AddNodeCommand(Context context, String[] parameters) {
        super(context, parameters);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean execute() {
        KVMessage response;
        HashSet<ServerNode> availableNodes = context.getECS().getAvailableNodes();
        if(availableNodes.isEmpty()){
            writeResponse("There are no free nodes available");
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
            kvECSCommInterface connection = new kvECSComm();
            try {
                connection.connect(newNode.getIpAddress(), newNode.getPort());
                context.getConnections().add(connection);
                metaData.addServer(newNode);
                context.getECS().setMetaData(metaData);
                connection.sendMessage(
                        kvMessageBuilder.buildUpdateMetaDataMessage(
                                metaData, newNode.getName()));
                connection.sendMessage(
                        kvMessageBuilder.buildStartMessage());
                
                //Ask Successor to Move Data
                ServerNode successor = 
                        metaData.getPredecessor(newNode.getHash());//SUCCESSOR?
                kvECSCommInterface suConnection = getNodeConnection(successor);
                suConnection.sendMessage(
                        kvMessageBuilder.buildWriteLockMessage());
                suConnection.sendMessage(
                        kvMessageBuilder.buildUpdateMetaDataMessage(
                                context.getECS().getMetaData(),
                                successor.getName()));
                response = suConnection.sendMessage(
                        kvMessageBuilder.buildMoveMessage());
                if (response.getStatus().equals(KVMessage.StatusType.ACK)){
                    //Broadcast MetaData Update
                    for (kvECSCommInterface con : context.getConnections()){
                        con.sendMessage(
                                kvMessageBuilder.buildUpdateMetaDataMessage(
                                        context.getECS().getMetaData(),
                                        successor.getName()));
                    }
                    //Release Lock
                    suConnection.sendMessage(
                            kvMessageBuilder.buildReleaseLockMessage());
                }
                return true;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
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
        if(parameters.length==0){
            return true;
        }
        return false;
    }
}
