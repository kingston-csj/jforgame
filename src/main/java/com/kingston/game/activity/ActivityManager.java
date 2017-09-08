package com.kingston.game.activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import com.kingston.game.database.user.player.Player;
import com.kingston.utils.NameableThreadFactory;


public class ActivityManager {
	
	private static ActivityManager instance = new ActivityManager();
	
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(
													new NameableThreadFactory("activity-scheduler"));
	
	public static ActivityManager getInstance() {
		return instance;
	}
	
	public void openMainPanel(Player player, int activityId) {
		
	}

}
