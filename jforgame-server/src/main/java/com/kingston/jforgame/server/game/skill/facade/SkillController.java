package com.kingston.jforgame.server.game.skill.facade;

import com.kingston.jforgame.server.game.skill.message.ReqUseSkillMessage;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class SkillController {

	@RequestMapping
	public void reqUseSkill(long playerId, ReqUseSkillMessage request) {
	}

}
