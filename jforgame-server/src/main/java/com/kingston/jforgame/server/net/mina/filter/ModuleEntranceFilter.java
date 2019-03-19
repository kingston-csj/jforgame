package com.kingston.jforgame.server.net.mina.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.server.game.core.BaseNotify;
import com.kingston.jforgame.server.game.notice.message.ResSystemNotice;
import com.kingston.jforgame.server.net.NetGateKeeper;
import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.message.MessageFactory;
import com.kingston.jforgame.socket.message.MessagePusher;

/**
 * 功能模块请求过滤器
 * @author kingston
 */
public class ModuleEntranceFilter extends IoFilterAdapter {

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object packet) throws Exception {
		Message message = (Message)packet;
		int messageId = MessageFactory.INSTANCE.getMessageId(message.getClass());

		if (NetGateKeeper.getInstance().canVisit(messageId)) {
			nextFilter.messageReceived(session, message);
		} else  {
			// 模块功能出了bug，暂时把功能入口关闭了，等bug修复以后再重新打开
			ResSystemNotice notice = ResSystemNotice.valueOf(BaseNotify.FUNCTION_NOT_OPEN);
//			MessagePusher.pushMessage(session, notice);
		}
	}

}
