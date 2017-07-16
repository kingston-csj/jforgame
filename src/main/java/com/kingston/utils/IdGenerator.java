package com.kingston.utils;

import java.util.concurrent.atomic.AtomicLong;


public class IdGenerator {
	
	private static AtomicLong generator = new AtomicLong(10000);
	
	/**
	 * 生成全局唯一id
	 */
	public static long getUid() {
		//临时策略
		return generator.getAndIncrement();
	}

}
