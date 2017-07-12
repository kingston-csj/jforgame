package com.kingston.net;

import org.apache.mina.core.session.AttributeKey;

public interface SessionProperties {

	/** 编码上下文 */
	AttributeKey CODEC_CONTEXT = new AttributeKey(SessionProperties.class, "CONTEXT_KEY");
	/** 线程池分发器的索引 */
	AttributeKey DISTRIBUTE_KEY = new AttributeKey(SessionProperties.class, "DISTRIBUTE_KEY");
	
	
}
