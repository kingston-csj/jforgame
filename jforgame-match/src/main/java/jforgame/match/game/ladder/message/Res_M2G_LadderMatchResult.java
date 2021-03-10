package jforgame.match.game.ladder.message;

import java.util.Set;

import jforgame.match.game.ladder.model.LadderMatchVo;
import jforgame.socket.message.Message;

public class Res_M2G_LadderMatchResult extends Message {

	private Set<LadderMatchVo> matches;

	public static Res_M2G_LadderMatchResult valueOf(Set<LadderMatchVo> matches) {
		Res_M2G_LadderMatchResult result = new Res_M2G_LadderMatchResult();
		result.matches = matches;

		return result;
	}

	public Set<LadderMatchVo> getMatches() {
		return matches;
	}

	public void setMatches(Set<LadderMatchVo> matches) {
		this.matches = matches;
	}

}
