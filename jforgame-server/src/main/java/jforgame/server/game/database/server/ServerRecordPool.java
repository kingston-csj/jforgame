package jforgame.server.game.database.server;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public enum ServerRecordPool {

	/** 枚举单例 */
	INSTANCE;

	private ConcurrentMap<String,String> serverRecords = new ConcurrentHashMap<>();

	public void loadAllRecords() {

	}

	/**
	 * 获取开服天数
	 * @return
	 */
	public int getOpenServerDays() {
		return 1;
	}

	public Date getOpenServerDate() {
		try {
			return DateUtils.parseDate("2021-04-10 01:00:00","yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取合服天数
	 * @return
	 */
	public int getMergedServerDays() {
		return 1;
	}

	public Date getMergedServerDate() {
		try {
			return DateUtils.parseDate("2021-05-10 01:00:00","yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
