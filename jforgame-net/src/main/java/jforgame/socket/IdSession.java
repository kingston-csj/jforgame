package jforgame.socket;

import jforgame.socket.message.Message;
import org.apache.mina.core.session.IoSession;

import io.netty.channel.Channel;

/**
 * 玩家登录session，不与任何nio框架绑定
 * 
 * @see IoSession
 * @see Channel
 * 
 * @author kinson
 */
public interface IdSession {

	String ID = "ID";

	void sendPacket(Message packet);

	long getOwnerId();

	/**
	 * 更新属性值
	 * @param key
	 * @param value
	 * @return
	 */
	Object setAttribute(String key, Object value);

	/**
	 * 修改属性值
	 * @param key
	 * @return
	 */
	Object getAttribute(String key);

}
