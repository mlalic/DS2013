package app_kvEcs.communication;

import common.communication.Session;
import common.communication.TcpSession;
import common.messages.KVMessage;
import common.messages.KVMessageMarshaller;
import common.metadata.ServerNode;

public class TcpNodeCommunicator extends NodeCommunicator {

    private Session session;
    private KVMessageMarshaller marshaller = new KVMessageMarshaller();

    public TcpNodeCommunicator(ServerNode node) {
    	super(node);
    }
    
    @Override
    public void connect() throws Exception {
        session = new TcpSession(node.getIpAddress(), node.getPort());
        session.connect();
    }

    @Override
    public void disconnect() {
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

}
