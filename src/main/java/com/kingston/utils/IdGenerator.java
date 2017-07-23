package com.kingston.utils;

import java.util.concurrent.atomic.AtomicLong;

import com.kingston.ServerConfig;

/**
 * 全局id生成器
 * @author kingston
 */
public class IdGenerator {
	
	private static AtomicLong generator = new AtomicLong(0);
	
	/**
	 * 生成全局唯一id
	 */
	public static long getUid() {
		//----------------id格式 -------------------------
		//----------long类型8个字节64个比特位----------------
		// 高16位          | 中32位          | 低16位
		// serverId   系统毫秒数        自增长号
		
		long serverId = (long)ServerConfig.getInstance().getServerId(); 
		//临时策略
		return  (serverId << 48)
			  |	(((System.currentTimeMillis()/1000) & 0xFFFFFFFF) << 16)
			  | (generator.getAndIncrement() & 0xFFFF);
	}

}
