package com.kingston.jforgame.server.match;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.cross.core.match.MatchHttpUtil;
import com.kingston.jforgame.server.cross.core.match.ladder.message.MReqLadderApplyMessage;
import com.kingston.jforgame.socket.message.Message;


public class MatchHttpTest {

	@Before
	public void init() {
		ServerConfig.getInstance().init();
	}

	@Test
	public void httpRquest() throws IOException {
		Message response = MatchHttpUtil.submit(new MReqLadderApplyMessage());

		System.err.println("收到响应<<<<<<<<<" + response);
	}
}
