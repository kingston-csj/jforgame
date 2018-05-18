package com.kingston.jforgame.match.game.ladder.facade;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.match.core.HttpMessagePusher;
import com.kingston.jforgame.match.game.ladder.message.MReqLadderApplyMessage;
import com.kingston.jforgame.match.game.ladder.message.MResLadderApplySuccMessage;
import com.kingston.jforgame.socket.annotation.Controller;
import com.kingston.jforgame.socket.annotation.RequestMapping;

@Controller
public class LadderController {

	@RequestMapping
	public void apply(IoSession session, MReqLadderApplyMessage request) {
		HttpMessagePusher.push(session, new MResLadderApplySuccMessage());
	}

}
