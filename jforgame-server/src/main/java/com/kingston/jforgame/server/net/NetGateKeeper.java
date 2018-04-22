package com.kingston.jforgame.server.net;

import com.kingston.jforgame.common.utils.ConcurrentHashSet;

public class NetGateKeeper {

	private static NetGateKeeper instance = new NetGateKeeper();

	/** 暂时受限的协议列表  */
	private ConcurrentHashSet<Integer> forbidProcotols = new ConcurrentHashSet<>();

	public static NetGateKeeper getInstance() {
		return instance;
	}

	public void openProcotol(int messageId) {
		forbidProcotols.add(messageId);
	}

	public void closeProcotol(int messageId) {
		forbidProcotols.add(messageId);
	}

	public boolean canVisit(int messageId) {
		return ! forbidProcotols.contains(messageId);
	}

}
