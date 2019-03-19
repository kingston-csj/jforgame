package com.kingston.jforgame.socket.message;

import java.util.Collection;

import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.session.SessionManager;


/**
 * util used to push message to client
 * @author kingston
 */
public class MessagePusher {

	public static void pushMessage(long playerId, Message message) {
		IdSession userSession = SessionManager.INSTANCE.getSessionBy(playerId);
		pushMessage(userSession, message);
	}

	public static void pushMessage(Collection<Long> playerIds, Message message) {
		for (long playerId:playerIds) {
			pushMessage(playerId, message);
		}
	}

	public static void pushMessage(IdSession session, Message message) {
		if (session == null || message == null) {
			return;
		}
		session.sendPacket(message);
	}

}
