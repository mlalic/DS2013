package performance;

import client.KVStore;

public class ClientThread extends Thread {
	
	public static final String KEY_NAME = "key";
	public static final String VALUE_NAME = "value";
	
	private String address;
	private int port;
	private int scope;
	private String command;
	
	public ClientThread(String address, int port, int scope, String command) {
		this.address = address;
		this.port = port;
		this.scope = scope;
		this.command = command;
	}
	
	public void run() {
		KVStore kvClient = new KVStore(address, port);		
		try {
			kvClient.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < 10000; i++) {
			try {
				if ("put".equals(command)) {
					kvClient.put(KEY_NAME + (i + scope), VALUE_NAME + (i + scope));
				}
				else {
					kvClient.get(KEY_NAME + (i + scope));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		kvClient.disconnect();
	}
	
}
