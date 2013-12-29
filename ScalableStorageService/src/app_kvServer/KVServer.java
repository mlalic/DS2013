package app_kvServer;

import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import app_kvEcs.communication.kvMessageBuilder;
import common.communication.NodeCommunicator;
import common.communication.TcpNodeCommunicator;
import common.messages.KVMessage;
import common.messages.KVMessage.StatusType;
import common.metadata.MetaData;
import common.metadata.ServerNode;


public class KVServer extends Thread {
	
	private static final long REPLICA_UPDATE_DELAY = 10;  // in seconds
	private static Logger logger = Logger.getRootLogger();
	
	private final class UpdateReplicasTask implements Runnable {
		private HashSet<ServerNode> existingReplicas = new HashSet<ServerNode>();

		@Override
		public void run() {
			logger.info("Updating replicas...");
			Map<String, String> dirtyEntries = serverContext.getAndClearDirtyEntries();
			List<ServerNode> replicas = serverContext.getReplicas();
			
			for (ServerNode replica: replicas) {
				if (existingReplicas.contains(replica)) {
					updateExistingReplica(replica, dirtyEntries);
				} else {
					updateNewReplica(replica);
				}
			}
			
			existingReplicas = new HashSet<ServerNode>(replicas);
		}

		private void updateNewReplica(ServerNode replica) {
			Map<String, String> data = serverContext.getServerStorage().getAllData();
			if (data.size() == 0) {
				// Nothing to be done...
				return;
			}

			logger.debug("Updating a new replica " + replica.getName() + " " + replica.getNodeAddress());
			performReplicaUpdate(replica, data);
		}

		private void updateExistingReplica(ServerNode replica, Map<String, String> dirtyEntries) {
			if (dirtyEntries.size() == 0) {
				// Nothing to be done: don't waste time and resources on setting up
				// and tearing down TCP connections and sending empty messages
				return;
			}

			logger.debug("Updating an existing replica " + replica.getName() + " " + replica.getNodeAddress());
			performReplicaUpdate(replica, dirtyEntries);
		}

		private void performReplicaUpdate(ServerNode replica, Map<String, String> updatedEntries) {
			NodeCommunicator communicator = new TcpNodeCommunicator(replica);
			logger.info("Updating replica " + replica.getName());

			try {
				communicator.connect();
			} catch (Exception e) {
				logger.error("Unable to establish a connection with the replica.");
				return;
			}
			KVMessage response = communicator.sendMessage(
					kvMessageBuilder.buildUpdateReplicatedDataMessage(updatedEntries));

			communicator.disconnect();
			
			if (response == null) {
				logger.error("No response received from the replica " + replica.getName());
			} else if (response.getStatus() != StatusType.ACK) {
				// Replica unable to process the request...
				logger.error("Unable to update the replica " + replica.getName());
			} else {
				// Successfully updated the replica...
				logger.info("Successfully updated the replica " + replica.getName());
			}
		}
	}

	public enum ServerStatusType {
		IDLE,
		STARTED,
		LOCKED_WRITE,
		STOPPED
	}
	
	private ServerSocket serverSocket = null;
	private int port;
	private boolean running;
	private ServerContext serverContext;
	
	/**
	 * Start KV Server at given port
	 * @param port given port for storage server to operate
	 */
	public KVServer(int port, String nodeName) {
		this.port = port;
		this.serverContext = new ServerContext(nodeName, ServerStatusType.IDLE);
	}

    /**
     * Initializes and starts the server. 
     * Loops until the the server should be closed.
     */
    public void run() {
        
    	running = initializeServer();
    	
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(
				Runtime.getRuntime().availableProcessors());
    	executor.scheduleWithFixedDelay(
    			new UpdateReplicasTask(),
    			REPLICA_UPDATE_DELAY,
    			REPLICA_UPDATE_DELAY,
    			TimeUnit.SECONDS);
        
        if(serverSocket != null) {
	        while(isRunning()){
	            try {
	                Socket client = serverSocket.accept();                
	                ClientConnection connection = new ClientConnection(client, serverContext);
	                new Thread(connection).start();
	                
	                logger.info("Connected to " 
	                		+ client.getInetAddress().getHostName() 
	                		+  " on port " + client.getPort());
	            } catch (IOException e) {
	            	logger.error("Error! " +
	            			"Unable to establish connection. \n", e);
	            }
	        }
        }
        logger.info("Server stopped.");
    }
    
    private boolean isRunning() {
        return this.running;
    }

    /**
     * Stops the server insofar that it won't listen at the given port any more.
     */
    public void stopServer(){
        running = false;
        try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Error! " +
					"Unable to close socket on port: " + port, e);
		}
    }

    private boolean initializeServer() {
    	logger.info("Initialize server ...");
    	try {
            serverSocket = new ServerSocket(port);
            logger.info("Server listening on port: " 
            		+ serverSocket.getLocalPort());    
            return true;
        
        } catch (IOException e) {
        	logger.error("Error! Cannot open server socket:");
            if(e instanceof BindException){
            	logger.error("Port " + port + " is already bound!");
            }
            return false;
        }
    }
    
    /**
     * Main entry point for the echo server application. 
     * @param args contains the server(node) name at args[1] and the port number at args[0].
     */
    public static void main(String[] args) {
    	try {
			if(args.length != 2) {
                System.out.println("Error! Invalid number of arguments!");
				System.out.println("Usage: Server <port>!");
				
				logger.error("Error! Invalid number of arguments!");
			} else {
	    		String nodeName = args[1];
				int port = Integer.parseInt(args[0]);

				new LogSetup("logs/server/server" + "_" + nodeName + ".log", Level.ALL);
				new KVServer(port, nodeName).start();
			}
		} catch (IOException e) {
			System.out.println("Error! Unable to initialize logger!");
			logger.error("Error! Unable to initialize logger!");
			e.printStackTrace();
			System.exit(1);
		} catch (NumberFormatException nfe) {
			System.out.println("Error! Invalid argument <port>! Not a number!");
			System.out.println("Usage: Server <port>!");

			logger.error("Error! Invalid argument <port>! Not a number!");
			System.exit(1);
		}
		
		
    }
    
}
