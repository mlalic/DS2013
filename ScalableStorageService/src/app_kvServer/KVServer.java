package app_kvServer;

import java.io.IOException;
import java.net.*;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import common.metadata.MetaData;
import common.metadata.ServerNode;


public class KVServer extends Thread {
	
	private static Logger logger = Logger.getRootLogger();
	
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
    		String nodeName = args[1];
    		new LogSetup("logs/server/server" + "_" + nodeName + ".log", Level.ALL);
			if(args.length != 2) {
                System.out.println("Error! Invalid number of arguments!");
				System.out.println("Usage: Server <port>!");
				
				logger.error("Error! Invalid number of arguments!");
			} else {
				int port = Integer.parseInt(args[0]);
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
