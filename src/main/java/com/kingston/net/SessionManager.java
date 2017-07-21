package com.kingston.net;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

public enum SessionManager {
	
	INSTANCE;
	
	/** 分发器索引生成器 */
	private AtomicInteger distributeKeyGenerator = new AtomicInteger();
	
	/**
	 * 获取session对应的角色id
	 * @param session
	 * @return
	 */
	public long getPlayerId(IoSession session) {
		long result = 0;
		if (session != null) {
			
		}
		return result;
	}
	
	/**
	 * 获取session指定属性类型的值
	 * @param session
	 * @param attrKey
	 * @param attrType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T getSessionAttr(IoSession session, AttributeKey attrKey, Class<T> attrType) {
		return (T)session.getAttribute(attrKey);
	}
	
	public int getNextDistributeKey() {
		return this.distributeKeyGenerator.getAndIncrement();
	}

}
