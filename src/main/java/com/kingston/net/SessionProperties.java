package com.kingston.net;

import org.apache.mina.core.session.AttributeKey;

public interface SessionProperties {

	/** 上下文 */
	AttributeKey CONTEXT_KEY = new AttributeKey(SessionProperties.class, "CONTEXT_KEY");
}
