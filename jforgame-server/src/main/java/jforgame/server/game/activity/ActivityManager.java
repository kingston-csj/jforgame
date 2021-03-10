package jforgame.server.game.activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import jforgame.server.game.database.user.PlayerEnt;
import jforgame.common.thread.NamedThreadFactory;


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
