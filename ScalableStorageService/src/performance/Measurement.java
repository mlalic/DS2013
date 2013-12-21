package performance;

import client.KVStore;

public class Measurement {
	
	public static final String KEY_NAME = "key";
	public static final String VALUE_NAME = "value";
 	
	public static void main(String[] args) {
		
		Measurement measurement = new Measurement();
		measurement.test1();
		measurement.test2();
		measurement.test3();
		measurement.test4();
		
	}
	
	public void test1() {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		KVStore kvClient = new KVStore("localhost", 50000);		
		try {
			kvClient.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < 10000; i++) {
			try {
				kvClient.put(KEY_NAME + i, VALUE_NAME + i);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		kvClient.disconnect();
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing test1 (1 server, 1 client, 10000 data) is: " + (endTime - startTime)/1000);
	}
	
	public void test2() {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		KVStore kvClient1 = new KVStore("localhost", 50000);
		KVStore kvClient2 = new KVStore("localhost", 50000);
		KVStore kvClient3 = new KVStore("localhost", 50000);
		KVStore kvClient4 = new KVStore("localhost", 50000);
		KVStore kvClient5 = new KVStore("localhost", 50000);		
		try {
			kvClient1.connect();
			kvClient2.connect();
			kvClient3.connect();
			kvClient4.connect();
			kvClient5.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < 10000; i++) {
			try {
				kvClient1.put(KEY_NAME + i, VALUE_NAME + i);
				kvClient2.put(KEY_NAME + (i + 10000), VALUE_NAME + (i + 10000));
				kvClient3.put(KEY_NAME + (i + 20000), VALUE_NAME + (i + 20000));
				kvClient4.put(KEY_NAME + (i + 30000), VALUE_NAME + (i + 30000));
				kvClient5.put(KEY_NAME + (i + 40000), VALUE_NAME + (i + 40000));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		kvClient1.disconnect();
		kvClient2.disconnect();
		kvClient3.disconnect();
		kvClient4.disconnect();
		kvClient5.disconnect();
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing test2 (1 server, 5 clients, 10000 data/server) is: " + (endTime - startTime)/1000);
	}
	
	public void test3() {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		KVStore kvClient = new KVStore("localhost", 50000);
		
		try {
			kvClient.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < 10000; i++) {
			try {
				kvClient.put(KEY_NAME + (i * 20), VALUE_NAME + (i * 20));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		kvClient.disconnect();
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing test3 (3 servers, 1 client, 10000 data) is: " + (endTime - startTime)/1000);
	}
	
	public void test4() {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		KVStore kvClient1 = new KVStore("localhost", 50000);
		KVStore kvClient2 = new KVStore("localhost", 50000);
		KVStore kvClient3 = new KVStore("localhost", 50001);
		KVStore kvClient4 = new KVStore("localhost", 50002);
		KVStore kvClient5 = new KVStore("localhost", 50002);		
		try {
			kvClient1.connect();
			kvClient2.connect();
			kvClient3.connect();
			kvClient4.connect();
			kvClient5.connect();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for (int i = 0; i < 10000; i++) {
			try {
				kvClient1.put(KEY_NAME + i, VALUE_NAME + i);
				kvClient2.put(KEY_NAME + (i + 10000), VALUE_NAME + (i + 10000));
				kvClient3.put(KEY_NAME + (i + 20000), VALUE_NAME + (i + 20000));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < 10000; i++) {
			try {
				kvClient4.get(KEY_NAME + i);
				kvClient5.get(KEY_NAME + (i + 20000));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		kvClient1.disconnect();
		kvClient2.disconnect();
		kvClient3.disconnect();
		kvClient4.disconnect();
		kvClient5.disconnect();
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing test4 (3 servers, 5 clients, 10000 data/server) is: " + (endTime - startTime)/1000);
	}
}
