package jforgame.server.net;

import jforgame.common.utils.ConcurrentHashSet;

public class NetGateKeeper {

	private static NetGateKeeper instance = new NetGateKeeper();

	/** 暂时受限的协议列表  */
	private ConcurrentHashSet<Integer> forbidProtocols = new ConcurrentHashSet<>();

	public static NetGateKeeper getInstance() {
		return instance;
	}

	public void openProtocol(int messageId) {
		forbidProtocols.add(messageId);
	}

	public void closeProtocol(int messageId) {
		forbidProtocols.add(messageId);
	}

	public boolean canVisit(int messageId) {
		return ! forbidProtocols.contains(messageId);
	}

}
