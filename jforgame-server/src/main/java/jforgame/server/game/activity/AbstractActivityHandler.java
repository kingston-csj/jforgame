package jforgame.server.game.activity;

import jforgame.server.game.database.user.PlayerEnt;
import org.apache.commons.lang3.StringUtils;

import jforgame.socket.message.Message;

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
	public Message openPanel(PlayerEnt player) {
		return null;
	}

	@Override
	public void receiveRewards(PlayerEnt player) {
		
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
