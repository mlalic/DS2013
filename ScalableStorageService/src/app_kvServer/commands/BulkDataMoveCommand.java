package app_kvServer.commands;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import app_kvServer.ServerContext;

import com.google.gson.Gson;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.messages.KVMessageImpl;

public class BulkDataMoveCommand extends ServerCommand {

	private static Logger logger = Logger.getRootLogger();
	
	public BulkDataMoveCommand(String key, String value, final ServerContext serverContext) {
		super(key, value, serverContext);
	}
	
	@Override
	public KVMessage execute() {
		Map<String, String> data = unpackData(value);
		if (data == null) {
			return new KVMessageImpl(StatusType.COMM_ERROR);
		}
		for (Entry<String, String> entry: data.entrySet()) {
			serverContext.getServerStorage().put(entry.getKey(), entry.getValue());
		}
		
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
