package jforgame.server.game.skill.facade;

import jforgame.server.game.skill.message.ReqUseSkill;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

@Controller
public class SkillController {

	@RequestMapping
	public void reqUseSkill(long playerId, ReqUseSkill request) {
	}

}
