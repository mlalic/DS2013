package app_kvClient;

import client.KVStore;


public class KVClient {

    private final static String PROMPT = "EchoClient> ";
    

    public static void main(String[] args) {

    	KVStore store = new KVStore("localhost", 8000);
    	
    	try {
			store.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			store.put("key", "value");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	try {
			System.out.println(store.get("key").getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	try {
			store.put("key", "new-value");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	try {
			System.out.println(store.get("key").getValue());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	
    }

}
