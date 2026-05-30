package jforgame.demo.socket.filter;

import jforgame.demo.socket.GameMessageFactory;
import jforgame.demo.socket.NetGateKeeper;
import jforgame.socket.session.IdSession;
import jforgame.socket.dispatch.MessageHandler;
import jforgame.socket.dispatch.RequestContext;

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
