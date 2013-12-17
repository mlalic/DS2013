package app_kvServer;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import client.KVStore;
import app_kvServer.KVServer.ServerStatusType;
import app_kvServer.commands.ServerCommand;
import app_kvServer.commands.ServerCommandFactory;
import common.messages.KVMessage;
import common.messages.KVMessageImpl;
import common.messages.KVMessageMarshaller;
import common.messages.KVMessage.StatusType;
import common.metadata.MetaData;

public class ClientConnection implements Runnable {
	
	private static Logger logger = Logger.getRootLogger();
	private static final int MAX_MESSAGE_SIZE = 1024 * 120 + 20 + 1; // bytes

	private	final KVMessageMarshaller kvMessageMarshaller = new KVMessageMarshaller();
	
	private Socket clientSocket;
	private InputStream input;
	private OutputStream output;
	
	private boolean isOpen;
	
	private ServerContext serverContext;
	
	public ClientConnection(Socket clientSocket, ServerContext serverContext) {
		this.clientSocket = clientSocket;
		this.serverContext = serverContext;
		this.isOpen = true;
	}
	
	/**
	 * Get a message from the severStorage
	 * @param key - key value for HashMap
	 * @return String - value that is stored in the serverStorage with the given key 
	 * or null when the key is wrong
	 */
	public String get(String key) {
		return serverContext.getServerStorage().get(key);
	}
	
	
	/**
	 * Creates a {@link KVMessage} instance that indicates an error with the given
	 * error message.
	 * @param errorMessage The error message that will be returned to the client
	 */
	private KVMessage createErrorResponse(String errorMessage) {
		// Since the KVMessage interface does not provide us with a message type
		// to indicate errors with invalid responses, we use a dummy/placeholder
		// message type for now.
		final StatusType dummyErrorType = StatusType.DELETE_ERROR;
		return new KVMessageImpl(dummyErrorType, null, errorMessage);
	}
	
	/**
	 * Method writes the given {@link KVMessage} as a response to the given
	 * {@link OutputStream}
	 * @param output The stream to write the response to
	 * @param message The message to be sent back as a response
	 */
	private void writeResponse(OutputStream output, KVMessage message) throws IOException, Exception {
		output.write(kvMessageMarshaller.marshal(message));
	}

	/**
	 * Initializes and starts the client connection. 
	 * Loops until the connection is closed or aborted by the client.
	 */
	public void run() {
		// The factory instance to which proper command instantiation is delegated
		final ServerCommandFactory factory = new ServerCommandFactory(serverContext);		

		try {
			output = clientSocket.getOutputStream();
			input = clientSocket.getInputStream();
			
			while (isOpen) {
				try {
					
					//receive message (request)
			        byte[] data = readRawRequest(input);
			        if (data == null) {
			        	// Error -- no message could be read from the stream...
			        	logger.error("Error -- no message could be read from the stream...");
			        	writeResponse(output, createErrorResponse("Error reading the sent command"));
			        }

					// unmarshal message from array of bytes to KVMessage
			        KVMessage requestMessage = null;
			        try {
			        	requestMessage = kvMessageMarshaller.unmarshal(data);
			        } catch (Exception exc) {
			        	// Invalid command response ...
			        	logger.error("Invalid command response ...");
			        	writeResponse(output, createErrorResponse(exc.getMessage()));
			        	continue;
			        }
			        
			        logger.info("Server got a message from the client.");
					
					// create the command
					ServerCommand serverCommand = factory.createServerCommand(requestMessage);
					
					//execute command and get the responseMessage as the result
					KVMessage responseMessage = serverCommand.execute();
					
					logger.info("Server has successfully executed request and made response for the key: " 
							+ responseMessage.getKey() + " and value: " + responseMessage.getValue());
					
					// marshal KVMessage and send back to user
					writeResponse(output, responseMessage);
					
				/* connection either terminated by the client or lost due to 
				 * network problems*/	
				} catch (Exception ioe) {
					logger.error("Error! Connection lost!", ioe);
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
	
	/**
	 * Reads the incoming bytes off the given input stream
	 * @param input The input stream from which the request should be read
	 * @return The array of bytes read off the input stream representing a single request
	 * @throws IOException
	 */
	private byte[] readRawRequest(InputStream input) throws IOException {
		int alreadyRead = 0;
		final int bufferSize = 256;
		byte[] inData = new byte[MAX_MESSAGE_SIZE];
		byte[] inDataBuff = new byte[bufferSize];
        while (true) {
            int bytesRead = input.read(inDataBuff, 0, bufferSize);
            if (bytesRead == -1) {
            	// EOF
            	logger.info("End of file for the client connection reached...");
            	return null;
            }
            System.arraycopy(inDataBuff, 0, inData, alreadyRead, bytesRead);
            alreadyRead += bytesRead;
            if (alreadyRead >= MAX_MESSAGE_SIZE) {
                input.skip(input.available());
                break;
            }
            if (input.available() == 0) {
                break;
            }
		}
        if (alreadyRead == 0) {
        	return null;
        } else {
        	return Arrays.copyOf(inData, alreadyRead); 
        }
	}

}
