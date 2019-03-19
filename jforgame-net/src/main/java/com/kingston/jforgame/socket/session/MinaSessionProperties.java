package com.kingston.jforgame.socket.session;

import org.apache.mina.core.session.AttributeKey;

/**
 * session property enum
 * @author kingston
 */
public interface MinaSessionProperties {

	/** 编码上下文 */
	AttributeKey CODEC_CONTEXT = new AttributeKey(MinaSessionProperties.class, "CONTEXT_KEY");
	/** 洪水检查记录 */
	AttributeKey FLOOD = new AttributeKey(MinaSessionProperties.class, "FLOOD");
	/** 业务session */
	AttributeKey UserSession = new AttributeKey(MinaSessionProperties.class, "GameSession");
}
