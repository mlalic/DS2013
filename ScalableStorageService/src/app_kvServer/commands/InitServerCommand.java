package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import app_kvServer.ServerContext;
import common.messages.*;
import common.metadata.*;

public class InitServerCommand extends ServerCommand {
	
	private static Logger logger = Logger.getRootLogger();

	public InitServerCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}

	@Override
	public KVMessage execute() {
		try {
			MetaData metaData = MetaDataTransport.unmarshalMetaData(value);
			if (metaData == null) {
				responseMessage = new KVMessageImpl(KVMessage.StatusType.COMM_ERROR, key, value);
				logger.error("Meta data is null, error in communication.");
				return responseMessage;
			}
			serverContext.setMetaData(metaData);
			serverContext.setServerStatus(ServerStatusType.STOPPED);
			responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
			logger.info("Server has successfuly initianted.");
			return responseMessage;
		}
		catch (Exception e){
			responseMessage = new KVMessageImpl(KVMessage.StatusType.COMM_ERROR, key, value);
			logger.error("Exception trying to unmarschal mata data.");
			return responseMessage;
		}
	}
	
	public boolean isValid() {
		if (serverContext.getServerStatus().equals(ServerStatusType.IDLE) && serverContext.getMetaData() == null) {
			return true;
		}
		return false;
	}

}
