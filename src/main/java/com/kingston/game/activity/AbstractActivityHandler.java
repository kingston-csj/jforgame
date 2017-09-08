package com.kingston.game.activity;

import org.apache.commons.lang3.StringUtils;

import com.kingston.game.database.user.player.Player;
import com.kingston.net.Message;

public abstract class AbstractActivityHandler implements IActivityHandler {
	
	private Activity activity;
	
	@Override
	public Activity getActivity() {
		return activity;
	}
	
	@Override
	public void load() {
	}

	@Override
	public Message openPanel(Player player) {
		return null;
	}

	@Override
	public void receiveRewards(Player player) {
		
	}
	
	@Override
	public void checkPrepare() {
		if (!canOpen()) {
			return;
		}
		activityPrepare();
	}
	
	@Override
	public void checkStart() {
		if (!canOpen()) {
			return;
		}
		activityStart();
	}
	
	@Override
	public void checkEnd() {
		if (!activity.isOpened()) {
			return;
		}
		activityEnd();
	}
	

	@Override
	public boolean canOpen() {
		return true;
	}

	@Override
	public void activityPrepare() {
	}

	@Override
	public void activityStart() {
	}

	@Override
	public void activityEnd() {
	}
	
	
	@Override
	public void saveToDb(boolean fastSave) {
		Activity activity = getActivity();
		String key   = activity.getSerializeKey();
		String value = activity.serializeTostring();
		
		if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
			return;
		}
		
	}

}
