package jforgame.demo.game.skill.facade;

import jforgame.demo.game.skill.message.ReqUseSkill;
import jforgame.socket.core.protocol.annotation.MessageRoute;
import jforgame.socket.core.protocol.annotation.RequestHandler;

@MessageRoute
public class SkillController {

	@RequestHandler
	public void reqUseSkill(long playerId, ReqUseSkill request) {
	}

}
