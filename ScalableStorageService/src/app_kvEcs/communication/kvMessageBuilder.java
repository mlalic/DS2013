package app_kvEcs.communication;

import com.google.gson.Gson;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import common.metadata.MetaData;
import common.metadata.MetaDataTransport;
import common.metadata.ServerNode;

public class kvMessageBuilder {
    
    public static KVMessage buildUpdateMetaDataMessage(MetaData metaData){
        return new KVMessageImpl(
        		KVMessage.StatusType.UPDATE_METADATA,
        		null,
                MetaDataTransport.marshalMetaData(metaData));
    } 
    
    public static KVMessage buildStartMessage(){
        return new KVMessageImpl(KVMessage.StatusType.START_SERVER);
    }
    
    public static KVMessage buildStopMessage(){
        return new KVMessageImpl(KVMessage.StatusType.STOP_SERVER);
    }
    
    public static KVMessage buildShutdownMessage(){
        return new KVMessageImpl(KVMessage.StatusType.SHUT_DOWN);
    }
    
    public static KVMessage buildWriteLockMessage(){
        return new KVMessageImpl(KVMessage.StatusType.LOCK_WRITE);
    }
    
    public static KVMessage buildReleaseLockMessage(){
        return new KVMessageImpl(KVMessage.StatusType.UNLOCK_WRITE);
    }
    
    public static KVMessage buildMoveMessage(ServerNode destinationNode) {
    	Gson gson = new Gson();
    	String destinationNodeJson = gson.toJson(destinationNode);
        return new KVMessageImpl(KVMessage.StatusType.MOVE_DATA, null, destinationNodeJson);
    }
}
