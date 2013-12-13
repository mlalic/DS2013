package app_kvServer.commands;

import org.apache.log4j.Logger;

import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import common.metadata.MetaData;
import common.metadata.MetaDataTransport;

public class UpdateMetaDataCommandMessage extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	private MetaData metaData;
	
	public UpdateMetaDataCommandMessage(String key, String value, final MetaData metaData) {
		super(key, value);
		this.metaData = metaData;
	}

	@Override
	public KVMessage execute() {
		try {
			metaData = MetaDataTransport.unmarshalMetaData(value);
			if (metaData == null) {
				responseMessage = new KVMessageImpl(KVMessage.StatusType.COMM_ERROR, key, value);
				logger.error("Meta data is null, error in communication.");
				return responseMessage;
			}
			logger.info("Meta data is successfully updated.");
			responseMessage = new KVMessageImpl(KVMessage.StatusType.UPDATE_METADATA_ACK, key, value);
		} catch (Exception e) {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.COMM_ERROR, key, value);
			logger.error("Exception trying to unmarschal mata data.");
			return responseMessage;
		}
		return null;
	}

	@Override
	public boolean isValid() {
		if (metaData != null) {
			return true;
		}
		return false;
	}

}
