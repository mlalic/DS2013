package common.messages;

public class KVMessageImpl implements KVMessage {
	
	private KVMessage.StatusType statusType;
	private String key = null;
	private String value = null;
	
	public KVMessageImpl(KVMessage.StatusType statusType) {
		this.statusType = statusType;
	}
	
	public KVMessageImpl(KVMessage.StatusType statusType, String key, String value) {
		this.statusType = statusType;
		if (key == null) {
			this.key = "";
		} else {
			this.key = key;
		}
		if (value == null) {
			this.value = "";
		} else {
			this.value = value;
		}
	}

	@Override
	public String getKey() {
		return key;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public StatusType getStatus() {
		return statusType;
	}

}
