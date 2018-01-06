package com.kingston.jforgame.server.game.skill;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.net.socket.annotation.Controller;
import com.kingston.jforgame.net.socket.annotation.RequestMapping;
import com.kingston.jforgame.server.game.player.events.PlayerLevelUpEvent;
import com.kingston.jforgame.server.game.skill.message.ReqUseSkillMessage;
import com.kingston.jforgame.server.listener.EventType;
import com.kingston.jforgame.server.listener.annotation.EventHandler;
import com.kingston.jforgame.server.listener.annotation.Listener;

@Controller
@Listener
public class SkillFacade {


	@RequestMapping
	public void reqUseSkill(IoSession session, ReqUseSkillMessage request) {
	}

	@EventHandler(value=EventType.LEVEL_UP)
	public void onPlayerLevelup(PlayerLevelUpEvent levelUpEvent) {
		System.err.println(getClass().getSimpleName()+"捕捉到事件"+levelUpEvent);
	}

}
