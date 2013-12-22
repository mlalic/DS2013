package performance;

import java.util.*;

import client.KVStore;

public class Measurement {
	
	public static final String KEY_NAME = "key";
	public static final String VALUE_NAME = "value";
 	
	public static void main(String[] args) {
		
		Measurement measurement = new Measurement();
		
		/**
		 * Testing one server with many clients
		 */
		//measurement.testOneServer(1);
		//measurement.testOneServer(5);
		//measurement.testOneServer(10);
		//measurement.testOneServer(15);
		
		/**
		 * Testing three servers with many clients
		 */
		//measurement.testThreeServers(1);
		//measurement.testThreeServers(5);
		//measurement.testThreeServers(10);
		//measurement.testThreeServers(15);
		
		/**
		 * Testing five servers with many clients
		 */
		//measurement.testFiveServers(1);
		//measurement.testFiveServers(5);
		//measurement.testFiveServers(10);
		//measurement.testFiveServers(15);
		
		/**
		 * Testing constant data set with three servers, n clients
		 */
		//measurement.testThreeServersWithConstantData(1);
		//measurement.testThreeServersWithConstantData(5);
		//measurement.testThreeServersWithConstantData(10);
		measurement.testThreeServersWithConstantData(15);
		
	}
	
	/**
	 * Testing one server, n clients
	 */
	public void testOneServer(int n) {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		List<ClientThread> threads = new LinkedList<ClientThread>(); 		
		for (int i = 0; i < n; i++) {
			int scope = i * 10000;
			ClientThread client = new ClientThread("127.0.0.1", 50000, scope, "put", 10000);
			threads.add(client);
			client.start();
		}
		
		for (ClientThread client : threads) {
			try {
				client.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing testOneServer (1 server, " + n + " clients, 10000 data) is: " + (endTime - startTime)/1000 + " seconds ");
		
	}
	
	/**
	 * Testing three servers, n clients
	 */
	public void testThreeServers(int n) {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		List<ClientThread> threads = new LinkedList<ClientThread>(); 		
		for (int i = 0; i < n; i++) {
 			int scope = i * 10000;
			int port = 50000  + (i % 3);
			ClientThread client = new ClientThread("127.0.0.1", port, scope, "put", 10000);
			threads.add(client);
			client.start();
		}
		
		for (ClientThread client : threads) {
			try {
				client.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing testThreeServers (3 servers, " + n + " clients, 10000 data) is: " + (endTime - startTime)/1000 + " seconds ");
	}
	
	/**
	 * Testing five servers, n clients
	 */
	public void testFiveServers(int n) {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		List<ClientThread> threads = new LinkedList<ClientThread>(); 		
		for (int i = 0; i < n; i++) {
 			int scope = i * 10000;
			int port = 50000  + (i % 5);
			ClientThread client = new ClientThread("127.0.0.1", port, scope, "put", 10000);
			threads.add(client);
			client.start();
		}
		
		for (ClientThread client : threads) {
			try {
				client.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing testFiveServers (5 servers, " + n + " clients, 10000 data) is: " + (endTime - startTime)/1000 + " seconds ");
	}
	
	/**
	 * Testing constant data set with three servers, n clients
	 */
	public void testThreeServersWithConstantData(int n) {
		long startTime;
		long endTime;
		
		startTime = System.currentTimeMillis();
		
		List<ClientThread> threads = new LinkedList<ClientThread>(); 
		int data = 1000000 / n;
		for (int i = 0; i < n; i++) {
 			int scope = i * data;
			int port = 50000  + (i % 3);
			ClientThread client = new ClientThread("127.0.0.1", port, scope, "put", data);
			threads.add(client);
			client.start();
		}
		
		for (ClientThread client : threads) {
			try {
				client.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		endTime = System.currentTimeMillis();	
		
		System.out.println("Time for executing testThreeServersWithConstantData (3 servers, " + n + " clients, 10000 data) is: " + (endTime - startTime)/1000 + " seconds ");
	}
	
}
