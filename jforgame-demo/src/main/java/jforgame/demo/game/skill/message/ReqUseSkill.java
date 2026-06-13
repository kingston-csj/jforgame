package jforgame.demo.game.skill.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.skill.SkillDataPool;
import jforgame.socket.core.protocol.annotation.MessageMeta;
import jforgame.socket.core.protocol.message.Message;

@MessageMeta(module=Modules.SKILL, cmd=SkillDataPool.REQ_USE_SKILL)
public class ReqUseSkill implements Message {

	private int skillId;

	public int getSkillId() {
		return skillId;
	}

	public void setSkillId(int skillId) {
		this.skillId = skillId;
	}

	@Override
	public String toString() {
		return "ReqUseSkillMessage [skillId=" + skillId + "]";
	}

}
