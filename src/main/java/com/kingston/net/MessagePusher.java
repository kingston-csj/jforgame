package com.kingston.net;

import org.apache.mina.core.session.IoSession;

/**
 * 消息推送器
 * @author kingston
 */
public class MessagePusher {
	
	
	public static void pushMessage(IoSession session, Message message) {
		session.write(message);
	}

}
