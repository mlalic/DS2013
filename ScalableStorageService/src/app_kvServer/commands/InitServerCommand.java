package app_kvServer.commands;

import org.apache.log4j.Logger;

import app_kvServer.KVServer.ServerStatusType;
import common.messages.*;
import common.metadata.*;

public class InitServerCommand extends ServerCommand {
	
	private static Logger logger = Logger.getRootLogger();
	
	private MetaData metaData;
	private ServerStatusType serverStatus;

	public InitServerCommand(String key, String value, final MetaData metaData, final ServerStatusType serverStatus) {
		super(key, value);
		this.metaData = metaData;
		this.serverStatus = serverStatus;
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
			serverStatus = ServerStatusType.STOPPED;
			responseMessage = new KVMessageImpl(KVMessage.StatusType.INIT_ACK, key, value);
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
		if (serverStatus.equals(ServerStatusType.IDLE) && metaData == null) {
			return true;
		}
		return false;
	}

}
