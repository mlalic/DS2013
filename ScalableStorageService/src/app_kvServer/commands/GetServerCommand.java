package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import app_kvServer.ServerContext;
import common.messages.*;
import common.metadata.*;

public class GetServerCommand extends ServerCommand {
	
	private static Logger logger = Logger.getRootLogger();

	public GetServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	/**
	 * Get the message from the serverStorage with the given name
	 * If we find a message with the given key, then status is GET_SUCCESS
	 * If we don't find a message with the given name then staus is GET_ERROR 
	 */
	@Override
	public KVMessage execute() {
		if (serverContext.getServerStatus().equals(ServerStatusType.STARTED) || serverContext.getServerStatus().equals(ServerStatusType.LOCKED_WRITE)) {
			if (isResponsibleFor(key)) {
				String value = serverContext.getServerStorage().get(key);
				if (value == null) {
					responseMessage = new KVMessageImpl(
							KVMessage.StatusType.GET_ERROR,
							key,
							"Key '" + key + "' not found");
					logger.info("Server has executed GET_ERROR");
					return responseMessage;
				}
				else {
					responseMessage = new KVMessageImpl(KVMessage.StatusType.GET_SUCCESS, key, value);
					logger.info("Server has executed GET_SUCCESS");
					return responseMessage;
				}
			} else {
				// If the server is not responsible for that range, it is sending new updated meta data to the client
				String transportMetaData = MetaDataTransport.marshalMetaData(serverContext.getMetaData());
				responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_NOT_RESPONSIBLE, key, transportMetaData);
				logger.info("Server is not responsible for that key range. Updated meta data has been sent.");
				return responseMessage;
			}
		} else if (serverContext.getServerStatus().equals(ServerStatusType.STOPPED)){
			responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_STOPPED, key, value);
			logger.info("Sever is stopped for serving requests.");
			return responseMessage;
		} else {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.SERVER_STOPPED, key, value);
			logger.info("Srver is not longer in thw ring, therefore he is not responsible any more.");
			return responseMessage;
		}
	}

	@Override
	public boolean isValid() {
		if (key == null) {
			return false;
		}
		return true;
	}

}
