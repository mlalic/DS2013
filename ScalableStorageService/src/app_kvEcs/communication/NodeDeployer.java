package app_kvEcs.communication;

import common.metadata.ServerNode;

public interface NodeDeployer {

	/**
	 * Starts up the given server node
	 * @param node
	 * @return
	 */
	public abstract boolean deploy(ServerNode node);

}