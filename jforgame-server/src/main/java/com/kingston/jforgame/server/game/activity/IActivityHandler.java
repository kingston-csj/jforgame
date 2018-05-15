package com.kingston.jforgame.server.game.activity;

import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.socket.message.Message;

public interface IActivityHandler {
	
	void load();
	
	Message openPanel(Player player);
	
	void receiveRewards(Player player);
	
	ActivityTypes getActvityType();
	
	Activity getActivity();
	
	boolean canOpen();
	
	void checkPrepare();
	
	void checkStart();
	
	void checkEnd();
	
	void activityPrepare();
	
	void activityStart();
	
	void activityEnd();
	
	/**
	 * 把活动数据持久化到数据库
	 * @param fastSave 是否马上入库
	 */
	void saveToDb(boolean fastSave);
	
	

}
