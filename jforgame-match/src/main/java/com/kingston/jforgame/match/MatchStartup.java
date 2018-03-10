package com.kingston.jforgame.match;

import com.kingston.jforgame.match.http.MatchServer;

public class MatchStartup {

	public static void main(String[] args) throws Exception {
		new MatchServer().start();
	}

}
