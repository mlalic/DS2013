package common.messages;

public interface KVMessage {
	
    public enum StatusType {
    	GET,             		/* Get - request */
    	GET_ERROR,       		/* requested tuple (i.e. value) not found */
    	GET_SUCCESS,     		/* requested tuple (i.e. value) found */
    	PUT,               		/* Put - request */
    	PUT_SUCCESS,     		/* Put - request successful, tuple inserted */
    	PUT_UPDATE,      		/* Put - request successful, i.e., value updated */
    	PUT_ERROR,       		/* Put - request not successful */
    	DELETE_SUCCESS,  		/* Delete - request successful */
    	DELETE_ERROR,     		/* Delete - request successful */    	
    	
    	SERVER_STOPPED,         /* Server is stopped, no requests are processed */
    	SERVER_WRITE_LOCK,      /* Server locked for out, only get possible */
    	SERVER_NOT_RESPONSIBLE,  /* Request not successful, server not responsible for key */   
    	
    	COMM_ERROR, 			/* Sever sends error (communication error) message */
    	INIT_SEREVER,			/* ECS initializes server and sends metadata */
    	INIT_ACK,				/* Server sends init acknowledgement to ECS */
    	SHUT_DOWN,				/* ECS requests server to shut down */
    	SHUT_DOWN_ACK,			/* Server sends shut down acknowledgement to ECS*/	
    	START_SERVER, 			/* ECS requests server to start working  */
    	START_ACK,				/* Server sends start acknowledgement to ECS */
    	STOP_SERVER,            /* ECS requests server to stop working  */
    	STOP_ACK,				/* Server sends stop acknowledgement to ECS */
    	LOCK_WRITE,				/* ECS locks writing on server */	
    	LOCK_WRITE_ACK,			/* Server sends lock write acknowledgement to ECS */
    	UNLOCK_WRITE,			/* ECS unlocks writing on server */
    	UNLOCK_WRITE_ACK,		/* Server sends unlock write acknowledgement to ECS */
    	MOVE_DATA,				/* ECS requests server to move data to other server */
    	UPDATE_METADATA,		/* ECS is sending new metadata to server */
    	UPDATE_METADATA_ACK		/* Server sends meta data update acknowledgement to ECS*/
}

	/**
	 * @return the key that is associated with this message, 
	 * 		null if not key is associated.
	 */
	public String getKey();
	
	/**
	 * @return the value that is associated with this message, 
	 * 		null if not value is associated.
	 */
	public String getValue();
	
	/**
	 * @return a status string that is used to identify request types, 
	 * response types and error types associated to the message.
	 */
	public StatusType getStatus();
	
}


