package jforgame.server.match;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import jforgame.server.ServerConfig;
import jforgame.server.game.cross.ladder.message.F2M_HeatBeat;


public class MatchHttpTest {

	@Before
	public void init() {
		ServerConfig.getInstance();
	}

	@Test
	public void httpRquest() throws IOException {
		AbstractMatchMessage response = MatchHttpUtil.submit(new F2M_HeatBeat());

		System.err.println("收到响应<<<<<<<<<" + response);
	}
}
