package client.communication;

import java.io.IOException;
import java.net.UnknownHostException;

public interface Session {
    
    /**
     * Initiates a TcpSession connection and initializes streams
     * @return true when the connection is successfully initiated
     * @return false when the connection is not successfully initiated
     * @throws IOException/UnknownHostException is connection fails
     */
    public boolean connect() throws IOException, UnknownHostException;
	
    /**
     * Disconnects the TcpSession connection and closes streams
     * @return true when connection is successfully terminated
     * @return false when connection is not successfully terminated
     */
	public boolean disconnect();
	
	/**
     * Sends an array of bytes to the output stream
     * @param outData an array of bytes to be sent over an open connection
     * @return true when the data is successfully sent
     * @return false when the data is not successfuly sent
     * @throws IOException if send fails
     */
	public boolean send(byte[] outData) throws IOException;
	
	/**
     * Receives data from the input stream
     * @return an array of bytes containing data read from the stream
     * @return null if no data is read
     * @throws IOException if read fails
     */
	public byte[] receive() throws IOException;
}
