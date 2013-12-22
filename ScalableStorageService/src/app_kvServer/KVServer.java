package app_kvServer;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;

import logger.LogSetup;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import common.metadata.MetaData;


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
	 * Start KV Server at given port
	 * Server Accepts Client Operations in initial state
	 * Used for Testing Purposes
	 * @param port given for storage server to operate
	 */
	public KVServer(int port, String nodeName, String status, MetaData metaData) {
	    this.port = port;
	    if (status.equalsIgnoreCase("started")){
	        this.serverContext = new ServerContext(nodeName, ServerStatusType.STARTED);
	        this.serverContext.setMetaData(metaData);
	    }
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
