package jforgame.server.cross.core.server;

import jforgame.server.cross.core.client.CCSession;
import jforgame.socket.share.message.Message;

public interface CMessageDispatcher {

	/**
	 * 服务端节点消息分发
	 * @param session
	 * @param message
	 */
	void serverDispatch(SCSession session, Object message);
	
	/**
	 * 服务端节点消息分发
	 * @param session
	 * @param message
	 */
	void clientDispatch(CCSession session, Object message);
	
}