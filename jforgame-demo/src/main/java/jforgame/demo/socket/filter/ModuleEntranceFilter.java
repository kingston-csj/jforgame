package jforgame.demo.socket.filter;

import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.NetGateKeeper;
import jforgame.socket.share.IdSession;
import jforgame.socket.share.MessageHandler;
import jforgame.socket.share.RequestContext;

/**
 * 功能模块请求过滤器
 *
 */
public class ModuleEntranceFilter implements MessageHandler {

    @Override
    public boolean messageReceived(IdSession session, RequestContext context) throws Exception {
        Object message = context.getRequest();
        int messageId = GameMessageFactory.getInstance().getMessageId(message.getClass());

        if (NetGateKeeper.getInstance().canVisit(messageId)) {
            return true;
        } else {
            return false;
        }
    }

}
