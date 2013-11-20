package app_kvServer;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import app_kvServer.commands.ServerCommand;
import app_kvServer.commands.ServerCommandFactory;
import common.messages.KVMessage;
import common.messages.KVMessageMarshaller;

public class ClientConnection implements Runnable {
	
	private static Logger logger = Logger.getRootLogger();
	
	private Socket clientSocket;
	private InputStream input;
	private OutputStream output;
	
	private boolean isOpen;
	
	private ConcurrentHashMap<String, String> serverStorage = new ConcurrentHashMap<String, String>();
	
	public ClientConnection(Socket clientSocket,ConcurrentHashMap<String, String> serverStorage) {
		this.clientSocket = clientSocket;
		this.serverStorage = serverStorage;
		isOpen = true;
	}
	
	/**
	 * Get a message from the severStorage
	 * @param key - key value for HashMap
	 * @return String - value that is stored in the serverStorage with the given key 
	 * or null when the key is wrong
	 */
	public String get(String key) {
		return serverStorage.get(key);
	}

	/**
	 * Initializes and starts the client connection. 
	 * Loops until the connection is closed or aborted by the client.
	 */
	public void run() {
		try {
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
			
			while(isOpen) {
				try {
					KVMessageMarshaller kvMessageMarshaller = new KVMessageMarshaller();
					
					//receive message (request)
					ByteArrayOutputStream buffer = new ByteArrayOutputStream(); 
					int nRead;
					byte[] data = new byte[1024 * 120 + 20 + 1 +1];
					while ((nRead = input.read(data, 0, data.length)) != -1) {
					  buffer.write(data, 0, nRead);
					}
					buffer.flush();

					// unmarshal message from array of bytes to KVMessage
					KVMessage requestMessage = kvMessageMarshaller.unmarshal(buffer.toByteArray());
					
					// create command factory as well as the command
					ServerCommandFactory factory = new ServerCommandFactory(serverStorage);
					ServerCommand serverCommand = factory.createServerCommand(requestMessage);
					
					//execute command and get the responseMessage as the result
					KVMessage responseMessage = serverCommand.execute();
					
					// marshal KVMessage and send back to user
					byte[] reusltResonseMessage = kvMessageMarshaller.marshal(responseMessage);
					output.write(reusltResonseMessage);
					
				/* connection either terminated by the client or lost due to 
				 * network problems*/	
				} catch (Exception ioe) {
					logger.error("Error! Connection lost!");
					isOpen = false;
				}				
			}
			
		} catch (IOException ioe) {
			logger.error("Error! Connection could not be established!", ioe);
			
		} finally {
			
			try {
				if (clientSocket != null) {
					input.close();
					output.close();
					clientSocket.close();
				}
			} catch (IOException ioe) {
				logger.error("Error! Unable to tear down connection!", ioe);
			}
		}
	}

}
