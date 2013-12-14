package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.ServerContext;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import common.metadata.MetaData;
import common.metadata.MetaDataTransport;

public class UpdateMetaDataCommandMessage extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public UpdateMetaDataCommandMessage(String key, String value, final ServerContext serverContext) {
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
			logger.info("Meta data is successfully updated.");
			responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
		} catch (Exception e) {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.COMM_ERROR, key, value);
			logger.error("Exception trying to unmarschal mata data.");
			return responseMessage;
		}
		return null;
	}

	@Override
	public boolean isValid() {
		if (serverContext.getMetaData() != null) {
			return true;
		}
		return false;
	}

}
