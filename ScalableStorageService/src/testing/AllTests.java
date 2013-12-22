package testing;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import logger.LogSetup;

import org.apache.log4j.Level;

import app_kvServer.KVServer;

import common.metadata.MetaData;
import common.metadata.ServerNode;


public class AllTests {

	static {
		try {
			new LogSetup("logs/testing/test.log", Level.ERROR);
	        MetaData metaData = new MetaData();
	        metaData.addServer(new ServerNode("node1", "localhost", 50000));
	        metaData.addServer(new ServerNode("node2", "localhost", 50001));
			new KVServer(50000, "node1", "started", metaData).start();
			new KVServer(50001, "node2", "started", metaData).start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static Test suite() {
		TestSuite clientSuite = new TestSuite("Basic Storage ServerTest-Suite");
		clientSuite.addTestSuite(ConnectionTest.class);
		clientSuite.addTestSuite(InteractionTest.class); 
		clientSuite.addTestSuite(AdditionalTest.class); 
		clientSuite.addTestSuite(MessageMarshalTest.class); 
		return clientSuite;
	}
	
}
