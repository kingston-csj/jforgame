package com.kingston.jforgame.server.match;

import java.io.IOException;

import org.junit.Test;

import com.kingston.jforgame.net.socket.message.Message;
import com.kingston.jforgame.server.match.ladder.message.MReqLadderApplyMessage;


public class TestMatchHttp {

	@Test
	public void httpRquest() throws IOException {
		Message response = MatchHttpUtil.submit(new MReqLadderApplyMessage());

		System.err.println("收到响应<<<<<<<<<" + response);
	}
}
