package com.kingston.jforgame.server.game.notice;

import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.notice.message.ResSystemNotice;
import com.kingston.jforgame.socket.message.MessagePusher;

public class NoticeUtil {
	
	public static void noticeToPlayer(Player player, int notifyId) {
		if (player == null || notifyId <= 0) {
			return;
		}
		ResSystemNotice notify = new ResSystemNotice();
		notify.setNoticeId(notifyId);
		MessagePusher.pushMessage(player.getId(), notify);
	}

}
