package client.communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;


public class TcpSession implements Session {
	
	Socket client;
	String host;
	int port; 
	boolean streams;
	InputStream is;
	OutputStream os;

    private final static int MAX_RESPONSE_SIZE = 122901; //1B+20B+120 KB
	
	/**
	 * Create an Instance of TcpSession 
	 * @param host which specifies the server address
	 * @param port	which specifies the port server is listening on
	 */
	public TcpSession(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	/**
	 * Initiates a TcpSession connection and initializes streams
	 * @return true when the connection is successfully initiated
	 * @return false when the connection is not successfully initiated
	 * @throws IOException/UnknownHostException is connection fails
	 */
	public boolean connect() throws IOException, UnknownHostException{
	    client = new Socket(host,port);
		streams = initStreams();
		return true;
	}

	/**
	 * Disconnects the TcpSession connection and closes streams
	 * @return true when connection is successfully terminated
	 * @return false when connection is not successfully terminated
	 */
	public boolean disconnect() {
		try{
			is.close();
			os.close();
			client.close();
			return true;
		}
		catch(IOException IOEx){
			return false;
		}
	}
	
	/**
	 * Initialize the input/output streams for the current connection
	 * @return true if the streams are successfully initialized
	 * @return false if the streams are not successfully initialized
	 */
	public boolean initStreams(){
		try{
			is = client.getInputStream();
			os = client.getOutputStream();
			return true;
		}
		catch(IOException IOEx){
			return false;
		}
	}

	/**
	 * Sends an array of bytes to the output stream
	 * @param outData an array of bytes to be sent over an open connection
	 * @return true when the data is successfully sent
	 * @return false when the data is not successfuly sent
	 * @throws IOException if send fails
	 */
	public boolean send(byte[] outData) throws IOException {
	    os.write(outData);
		os.flush();
		return true;
	}
	
	/**
	 * Receives data from the input stream
	 * @return an array of bytes containing data read from the stream
	 * @return null if no data is read
	 * @throws IOException if read fails
	 */
	public byte[] receive() throws IOException{
		int alreadyRead = 0;
		final int bufferSize = 256;
		byte[] inData = new byte[MAX_RESPONSE_SIZE];
		byte[] inDataBuff = new byte[bufferSize];
        while (true) {
            int bytesRead = is.read(inDataBuff, 0, bufferSize);
            System.arraycopy(inDataBuff, 0, inData, alreadyRead, bytesRead);
            alreadyRead += bytesRead;
            if (alreadyRead >= MAX_RESPONSE_SIZE) {
                is.skip(is.available());
                break;
            }
            if (is.available() == 0) {
                break;
            }
		}
		if(alreadyRead == 0){
			return null;
		}
		else{
            // Return only the data that was received from the server
			return Arrays.copyOf(inData, alreadyRead);
		}
	}

}