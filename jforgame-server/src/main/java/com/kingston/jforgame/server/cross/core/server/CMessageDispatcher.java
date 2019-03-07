package com.kingston.jforgame.server.cross.core.server;

import com.kingston.jforgame.server.cross.core.client.CCSession;
import com.kingston.jforgame.socket.message.Message;

public interface CMessageDispatcher {

	/**
	 * 服务端节点消息分发
	 * @param session
	 * @param message
	 */
	void serverDispatch(SCSession session, Message message);
	
	/**
	 * 服务端节点消息分发
	 * @param session
	 * @param message
	 */
	void clientDispatch(CCSession session, Message message);
	
}