package com.kingston.jforgame.match.game.ladder.facade;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.match.core.HttpMessagePusher;
import com.kingston.jforgame.match.game.ladder.message.Req_G2M_LadderApply;
import com.kingston.jforgame.match.game.ladder.message.Res_M2GLadderApplySucc;
import com.kingston.jforgame.match.game.ladder.message.Req_F2M_HeatBeat;
import com.kingston.jforgame.match.game.ladder.service.LadderCenterManager;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class LadderController {

	@RequestMapping
	public void heatBeat(IoSession session, Req_F2M_HeatBeat req) {
		System.out.println("收到心跳包-->" + req);
		LadderCenterManager.getInstance().updateFightServer(req.getServerId(), req.getInetIp(), req.getPort());
	}
	
	@RequestMapping
	public void apply(IoSession session, Req_G2M_LadderApply request) {
		HttpMessagePusher.push(session, new Res_M2GLadderApplySucc());
	}

}
