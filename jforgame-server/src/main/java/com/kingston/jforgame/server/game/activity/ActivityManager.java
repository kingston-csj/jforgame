package com.kingston.jforgame.server.game.activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.kingston.jforgame.common.thread.NamedThreadFactory;
import com.kingston.jforgame.server.game.database.user.player.PlayerEnt;


public class ActivityManager {

	private static volatile ActivityManager instance = new ActivityManager();

	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
													new NamedThreadFactory("activity-scheduler"));

	public static ActivityManager getInstance() {
		return instance;
	}

	public void openMainPanel(PlayerEnt player, int activityId) {

	}

}
