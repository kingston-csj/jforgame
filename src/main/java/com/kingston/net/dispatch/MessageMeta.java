package com.kingston.net.dispatch;

/** 
 * 消息的元信息
 * @author kingston
 */
class MessageMeta {

	//friendly访问权限，就不需要考虑getter/setter封装了
	
	/** 消息模块号 */
	short module = 0;
	/** 消息子类型 */
	short cmd = 0;

	static MessageMeta valueOf(short module, short cmd) {
		MessageMeta meta = new MessageMeta();
		meta.module = module;
		meta.cmd = cmd;

		return meta;
	}

}
