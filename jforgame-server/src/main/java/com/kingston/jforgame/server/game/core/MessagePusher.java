package com.kingston.jforgame.server.game.core;

import com.kingston.jforgame.server.game.database.config.ConfigDataPool;
import com.kingston.jforgame.server.game.database.config.bean.ConfigNotice;
import com.kingston.jforgame.server.game.database.config.storage.ConfigNoticeStorage;
import com.kingston.jforgame.socket.IdSession;
import com.kingston.jforgame.socket.message.Message;
import com.kingston.jforgame.socket.session.SessionManager;
import org.apache.mina.core.session.IoSession;

import java.util.Collection;

public class MessagePusher {

    public static void pushMessage(long playerId, Message message) {
        IdSession userSession = SessionManager.INSTANCE.getSessionBy(playerId);
        pushMessage(userSession, message);
    }

    public static void pushMessage(Collection<Long> playerIds, Message message) {
        for (long playerId:playerIds) {
            pushMessage(playerId, message);
        }
    }

    public static void pushMessage(IdSession session, Message message) {
        if (session == null || message == null) {
            return;
        }
        session.sendPacket(message);
    }

    public static void notify2Player(IoSession session, int i18nId) {
        ConfigNoticeStorage noticeStorage = ConfigDataPool.getInstance().getStorage(ConfigNoticeStorage.class);
        ConfigNotice idResource = noticeStorage.getNoticeBy(i18nId);
        if (idResource != null) {
//            MessagePusher.pushMessage(session, new RespMsg(idResource.getContent()));
        }
    }

    public static void notify2Player(IoSession session, int i18nId, Object... args) {
        ConfigNoticeStorage noticeStorage = ConfigDataPool.getInstance().getStorage(ConfigNoticeStorage.class);
        ConfigNotice idResource = noticeStorage.getNoticeBy(i18nId);
        if (idResource != null) {
            String content = String.format(idResource.getContent(), args);
        }
    }


}
