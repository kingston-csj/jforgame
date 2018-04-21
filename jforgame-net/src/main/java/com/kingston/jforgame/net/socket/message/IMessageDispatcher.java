package com.kingston.jforgame.net.socket.message;

import org.apache.mina.core.session.IoSession;

/**
 * 消息分发器
 * @author kingston
 */
public interface IMessageDispatcher {

	 /**
     * message entrance, in which io thread dispatch messages
     * @param session
     * @param message
     */
	void dispatch(IoSession session, Message message);
}
