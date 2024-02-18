package jforgame.server.socket.mina.filter;

import jforgame.server.game.core.BaseNotify;
import jforgame.server.game.notice.message.ResSystemNotice;
import jforgame.server.socket.NetGateKeeper;
import jforgame.socket.support.DefaultMessageFactory;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;

/**
 * 功能模块请求过滤器
 * @author kinson
 */
public class ModuleEntranceFilter extends IoFilterAdapter {

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		int messageId = DefaultMessageFactory.getInstance().getMessageId(message.getClass());

		if (NetGateKeeper.getInstance().canVisit(messageId)) {
			nextFilter.messageReceived(session, message);
		} else  {
			// 模块功能出了bug，暂时把功能入口关闭了，等bug修复以后再重新打开
			ResSystemNotice notice = ResSystemNotice.valueOf(BaseNotify.FUNCTION_NOT_OPEN);
		}
	}

}
