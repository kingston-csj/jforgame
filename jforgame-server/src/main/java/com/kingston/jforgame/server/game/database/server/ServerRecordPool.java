package com.kingston.jforgame.server.game.database.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum ServerRecordPool {

	INSTANCE;

	private ConcurrentMap<String,String> serverRecords = new ConcurrentHashMap<>();

	public void loadAllRecords() {

	}

}
