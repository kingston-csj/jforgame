package com.kingston.jforgame.server.game.skill.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.skill.SkillDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module=Modules.SKILL, cmd=SkillDataPool.REQ_USE_SKILL)
public class ReqUseSkill extends Message {

	@Protobuf
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
