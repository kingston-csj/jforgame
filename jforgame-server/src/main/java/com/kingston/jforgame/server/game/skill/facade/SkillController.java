package com.kingston.jforgame.server.game.skill.facade;

import com.kingston.jforgame.net.socket.annotation.Controller;
import com.kingston.jforgame.net.socket.annotation.RequestMapping;
import com.kingston.jforgame.server.game.skill.message.ReqUseSkillMessage;

@Controller
public class SkillController {

	@RequestMapping
	public void reqUseSkill(long playerId, ReqUseSkillMessage request) {
	}

}
