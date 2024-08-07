package jforgame.demo.socket.filter;

import jforgame.demo.game.core.BaseNotify;
import jforgame.demo.game.notice.message.ResSystemNotice;
import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.NetGateKeeper;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.message.RequestDataFrame;

/**
 * 功能模块请求过滤器
 * @author kinson
 */
public class ModuleEntranceFilter implements MessageHandler {

	@Override
	public boolean messageReceived(IdSession session, Object frame)  throws Exception {
		RequestDataFrame dataFrame = (RequestDataFrame)frame;
		Object message = dataFrame.getMessage();
		int messageId = GameMessageFactory.getInstance().getMessageId(message.getClass());

		if (NetGateKeeper.getInstance().canVisit(messageId)) {
			return true;
		} else  {
			// 模块功能出了bug，暂时把功能入口关闭了，等bug修复以后再重新打开
			ResSystemNotice notice = ResSystemNotice.valueOf(BaseNotify.FUNCTION_NOT_OPEN);
			return false;
		}
	}

}
