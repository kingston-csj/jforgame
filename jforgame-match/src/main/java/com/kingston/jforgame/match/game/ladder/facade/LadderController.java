package com.kingston.jforgame.match.game.ladder.facade;

import java.util.Set;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.match.core.HttpMessagePusher;
import com.kingston.jforgame.match.game.ladder.message.Req_F2M_HeatBeat;
import com.kingston.jforgame.match.game.ladder.message.Req_G2M_LadderApply;
import com.kingston.jforgame.match.game.ladder.message.Req_G2M_LadderMatchResult;
import com.kingston.jforgame.match.game.ladder.message.Res_M2GLadderApplySucc;
import com.kingston.jforgame.match.game.ladder.message.Res_M2G_LadderMatchResult;
import com.kingston.jforgame.match.game.ladder.message.Res_M2G_HeatBeat;
import com.kingston.jforgame.match.game.ladder.model.LadderMatchVo;
import com.kingston.jforgame.match.game.ladder.service.LadderCenterManager;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

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
