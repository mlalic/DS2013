package app_kvEcs.communication;

import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import common.metadata.MetaData;
import common.metadata.MetaDataTransport;

public class kvMessageBuilder {
    
    public static KVMessage buildUpdateMetaDataMessage(MetaData metaData, String name){
        return new KVMessageImpl(KVMessage.StatusType.UPDATE_METADATA, name,
                MetaDataTransport.marshalMetaData(metaData));
    } 
    
    public static KVMessage buildStartMessage(){
        return new KVMessageImpl(KVMessage.StatusType.START_SERVER);
    }
}
