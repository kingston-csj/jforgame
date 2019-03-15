package com.kingston.jforgame.server.match;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.kingston.jforgame.server.ServerConfig;
import com.kingston.jforgame.server.cross.core.match.AbstractMatchMessage;
import com.kingston.jforgame.server.cross.core.match.MatchHttpUtil;
import com.kingston.jforgame.server.game.cross.ladder.message.Req_F2M_HeatBeat;


public class MatchHttpTest {

	@Before
	public void init() {
		ServerConfig.getInstance().init();
	}

	@Test
	public void httpRquest() throws IOException {
		AbstractMatchMessage response = MatchHttpUtil.submit(new Req_F2M_HeatBeat());

		System.err.println("收到响应<<<<<<<<<" + response);
	}
}
