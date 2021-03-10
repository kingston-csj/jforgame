package jforgame.match.game.ladder.facade;

import java.util.Set;

import jforgame.match.core.HttpMessagePusher;
import jforgame.match.game.ladder.message.Req_F2M_HeatBeat;
import jforgame.match.game.ladder.message.Req_G2M_LadderMatchResult;
import jforgame.match.game.ladder.message.Res_M2G_HeatBeat;
import jforgame.match.game.ladder.message.Res_M2G_LadderMatchResult;
import org.apache.mina.core.session.IoSession;

import jforgame.match.game.ladder.message.Req_G2M_LadderApply;
import jforgame.match.game.ladder.message.Res_M2GLadderApplySucc;
import jforgame.match.game.ladder.model.LadderMatchVo;
import jforgame.match.game.ladder.service.LadderCenterManager;
import jforgame.socket.annotation.Controller;
import jforgame.socket.annotation.RequestMapping;

@Controller
public class LadderController {

	@RequestMapping
	public void heatBeat(IoSession session, Req_F2M_HeatBeat req) {
		System.out.println("收到心跳包-->" + req);
		LadderCenterManager.getInstance().updateFightServer(req.getServerId(), req.getInetIp(), req.getPort());
		HttpMessagePusher.push(session, new Res_M2G_HeatBeat());
	}
	
	@RequestMapping
	public void apply(IoSession session, Req_G2M_LadderApply request) {
		HttpMessagePusher.push(session, new Res_M2GLadderApplySucc());
	}
	
	@RequestMapping
	public void apply(IoSession session, Req_G2M_LadderMatchResult req) {
		Set<LadderMatchVo> result = LadderCenterManager.getInstance().queryMatchResult(req.getServerId());
		HttpMessagePusher.push(session, Res_M2G_LadderMatchResult.valueOf(result));
	}

}
