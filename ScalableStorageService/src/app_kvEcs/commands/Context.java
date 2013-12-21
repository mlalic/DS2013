package app_kvEcs.commands;

import java.io.PrintStream;
import java.util.ArrayList;

import common.communication.NodeCommunicator;
import common.metadata.ServerNode;
import app_kvEcs.ECS;
import app_kvEcs.communication.NodeDeployer;
import app_kvEcs.communication.SshNodeDeployer;


public class Context {
    private ECS ecs = null;
    private PrintStream outputStream = null;
    private ArrayList<NodeCommunicator> connections = null;
	private NodeDeployer nodeDeployer;
   
    public ArrayList<NodeCommunicator> getConnections() {
        return connections;
    }

    public void setConnections(ArrayList<NodeCommunicator> connections) {
        this.connections = connections;
    }

    public ECS getECS() {
        return ecs;
    }

    public void setECS(ECS ecs) {
        this.ecs = ecs;
    }

    public PrintStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(PrintStream outputStream) {
        this.outputStream = outputStream;
    }
    
    public NodeCommunicator getNodeConnection(ServerNode node) {
    	// TODO Use a HashMap (since ordering of the connections isn't important when we need to access them all),
    	//      but make this lookup more efficient... 
        for (NodeCommunicator connection: connections) {
            if (connection.getServerNode().equals(node)) {
                return connection;
            }
        }
        return null;
    }

	public void setDeployer(NodeDeployer nodeDeployer) {
		this.nodeDeployer = nodeDeployer;
	}
	
	public NodeDeployer getDeployer() {
		return nodeDeployer;
	}

}
