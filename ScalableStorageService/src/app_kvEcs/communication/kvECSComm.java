package app_kvEcs.communication;

import java.io.IOException;

import common.communication.Session;
import common.communication.TcpSession;
import common.messages.KVMessage;
import common.messages.KVMessageMarshaller;

public class kvECSComm implements kvECSCommInterface {

    private Session session;
    private KVMessageMarshaller marsh;
    private String name;
    @Override
    public void connect(String ip, int port) throws Exception {
        session = new TcpSession(ip, port);
        session.connect();
        marsh = new KVMessageMarshaller();
    }

    @Override
    public void disconnect() {
        // TODO Auto-generated method stub
        session.disconnect();
   }

    @Override
    public KVMessage sendMessage(KVMessage message) {
        try {
            session.send(marsh.marshal(message));
            KVMessage message_recv = marsh.unmarshal(session.receive());
            return message_recv;
        } catch (Exception e) {
            // TODO Auto-generated catch block
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
        // TODO Auto-generated method stub
        return null;
    }

}
