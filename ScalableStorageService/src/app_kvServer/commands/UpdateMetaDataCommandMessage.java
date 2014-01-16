package app_kvServer.commands;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import app_kvServer.ServerContext;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import common.metadata.MetaData;
import common.metadata.MetaDataTransport;

public class UpdateMetaDataCommandMessage extends ServerCommand {

	private final class GarbageCollectStorageTask implements Runnable {
		@Override
		public void run() {
			Map<String, String> allData = serverContext.getServerStorage().getAllData();
			for (Entry<String, String> entry: allData.entrySet()) {
				final String entryKey = entry.getKey();
				if (!isResponsibleFor(entryKey) && !isReplicaFor(entryKey)) {
					serverContext.getServerStorage().remove(entryKey);
				}
			}
			
		}
	}

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
			
			// After each update of metadata, sweep the server storage and remove anything
			// that is no longer neither a replicated key/value nor a key/value for which the
			// node is directly responsible. Perform it in a separate thread, though, since this
			// is potentially a long-running operation, but there is no need to block the server
			// from responding to the request (and processing further requests), since the only
			// downside of having the extra data in the storage is that it takes up space, if
			// the server is neither responsible nor a replica of a certain key, it will not
			// respond to the get/put requests, regardless of the fact that the key is still
			// technically in the storage.
			// 
			// This is guaranteed to be thread safe due to the ServerStorage contract:
			// modifying the storage is thread safe && #getAllData returns a full copy of the data
			new Thread(new GarbageCollectStorageTask()).start();

			logger.info("Meta data is successfully updated.");
			responseMessage = new KVMessageImpl(KVMessage.StatusType.ACK, key, value);
		} catch (Exception e) {
			responseMessage = new KVMessageImpl(KVMessage.StatusType.COMM_ERROR, key, value);
			logger.error("Exception trying to unmarschal mata data.");
			return responseMessage;
		}
		return responseMessage;
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
