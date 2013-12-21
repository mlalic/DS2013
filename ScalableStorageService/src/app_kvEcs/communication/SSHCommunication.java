package app_kvEcs.communication;

import java.io.IOException;

import common.metadata.ServerNode;

public class SSHCommunication {
	private String remoteUser;
	// By default, the deployment script should be found in the current working directory of the executable
	private String scriptPath = "./";
	private final static String SCRIPT_NAME = "deploy.sh";
	
	public SSHCommunication() {
		// Sets up some default values for the parameters.
		this.remoteUser = "kvstore";
	}

	public SSHCommunication(String remoteUser) {
		this.remoteUser = remoteUser;
	}
	
	public SSHCommunication(String remoteUser, String scriptPath) {
		this.remoteUser = remoteUser;
		this.scriptPath = scriptPath;
	}
	
	public boolean deploy(ServerNode node) {
        try {
            Runtime run = Runtime.getRuntime();
            run.exec(buildCommand(node));
            return true;
        } catch (IOException exc) {
        	exc.printStackTrace();
        	return false;
        }
	}

	private String buildCommand(ServerNode node) {
		return String.format(
				"%s%s %s %s %s %s",
				scriptPath,
				SCRIPT_NAME,
				remoteUser,
				node.getIpAddress(),
				node.getPort(),
				node.getName());
	}

}
