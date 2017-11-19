package com.kingston.jforgame.net.socket.session;

import org.apache.mina.core.session.AttributeKey;

/**
 * session property enum
 * @author kingston
 */
public interface SessionProperties {

	/** 编码上下文 */
	AttributeKey CODEC_CONTEXT = new AttributeKey(SessionProperties.class, "CONTEXT_KEY");
	/** 线程池分发器的索引 */
	AttributeKey DISTRIBUTE_KEY = new AttributeKey(SessionProperties.class, "DISTRIBUTE_KEY");
	/** 玩家id */
	AttributeKey PLAYER_ID = new AttributeKey(SessionProperties.class, "PLAYER_ID");

}
