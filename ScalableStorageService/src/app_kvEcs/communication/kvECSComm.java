package app_kvEcs.communication;

import common.communication.Session;
import common.communication.TcpSession;
import common.messages.KVMessage;
import common.messages.KVMessageMarshaller;

public class kvECSComm implements kvECSCommInterface {

    private Session session;
    private KVMessageMarshaller marshaller = new KVMessageMarshaller();
    private String name;

    @Override
    public void connect(String ip, int port) throws Exception {
        session = new TcpSession(ip, port);
        session.connect();
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
        session.disconnect();
   }

    @Override
    public KVMessage sendMessage(KVMessage message) {
        try {
            session.send(marshaller.marshal(message));
            byte[] response = session.receive();
            if (response == null) {
            	// No response from the server...
            	return null;
            }
            return marshaller.unmarshal(response);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setHostName(String name){
        this.name = name;
    }
    
    @Override
    public String getHostName() {
    	return name;
    }

}
