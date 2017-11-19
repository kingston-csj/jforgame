package com.kingston.jforgame.net.socket.message;

import java.util.Collection;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.net.socket.session.SessionManager;


/**
 * util used to push message to client
 * @author kingston
 */
public class MessagePusher {

	public static void pushMessage(long playerId, Message message) {
		IoSession session = SessionManager.INSTANCE.getSessionBy(playerId);
		pushMessage(session, message);
	}

	public static void pushMessage(Collection<Long> playerIds, Message message) {
		for (long playerId:playerIds) {
			pushMessage(playerId, message);
		}
	}

	public static void pushMessage(IoSession session, Message message) {
		if (session == null || message == null) {
			return;
		}
		session.write(message);
	}


}
