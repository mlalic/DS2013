package app_kvServer.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import app_kvServer.ServerContext;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import common.messages.KVMessage.StatusType;

public class UpdateReplicatedDataCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public UpdateReplicatedDataCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}
	
	
	@Override
	public KVMessage execute() {
		Map<String, String> data = unpackData(value);
		if (data == null) {
			logger.error("Unable to update replicated data: invalid data format received.");
			return new KVMessageImpl(StatusType.COMM_ERROR);
		}
		for (Entry<String, String> entry: data.entrySet()) {
			if (entry.getValue().equals("null")) {
				serverContext.getServerStorage().remove(entry.getKey());
			} else {
				serverContext.getServerStorage().put(entry.getKey(), entry.getValue());
			}
		}
		logger.info("Updated replicated data");
		logger.debug("Received data " + value);
		
		return new KVMessageImpl(StatusType.ACK);
	}

	private Map<String, String> unpackData(String value) {
		Gson gson = new Gson();
		try {
			HashMap<String, String> data = gson.fromJson(value, HashMap.class);
			return data;
		} catch (Exception exc) {
			return null;
		}
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
