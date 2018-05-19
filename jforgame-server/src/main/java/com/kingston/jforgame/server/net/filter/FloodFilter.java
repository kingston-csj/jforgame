package com.kingston.jforgame.server.net.filter;

import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.kingston.jforgame.common.utils.NumberUtil;
import com.kingston.jforgame.common.utils.TimeUtil;
import com.kingston.jforgame.server.FireWallConfig;
import com.kingston.jforgame.server.net.model.FloodRecord;
import com.kingston.jforgame.socket.session.SessionManager;
import com.kingston.jforgame.socket.session.SessionProperties;

/**
 * 流量洪水过滤器
 * @author kingston
 *
 */
public class FloodFilter extends IoFilterAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(FloodFilter.class);
	
	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) 
			throws Exception {
		FloodRecord record = getFloodRecordBy(session);
		
		// 基础思路：玩家XX秒内累计洪水记录超过Y次，则判定为嫌疑人
		long now = System.currentTimeMillis();
		long currSecond = NumberUtil.intValue(now/TimeUtil.ONE_SECOND);
		long lastTime = record.getLastReceivedTime();
		int lastSecond = NumberUtil.intValue(lastTime/TimeUtil.ONE_SECOND);
		
		tryToResetFloodTimes(now, record);

		if (currSecond == lastSecond) {
			int packageSum = record.addSecondReceivedPackage();
			if (isMessageTooFast(packageSum)) {
				int floodTimes = record.addFloodTimes();
				if (isMeetFloodStandard(floodTimes)) {
					logger.error("session窗口期洪水记录超过上限");
					// TODO 注销session
				}
				record.setLastFloodTime(now);
				// 已经检查到洪水，则需要重置收包次数
				record.setReceivedPacksLastSecond(0);
			}
		} else {
			record.setReceivedPacksLastSecond(0);
		}
		
		record.setLastReceivedTime(now);
		
		nextFilter.messageReceived(session, message);
	}
	
	private static FloodRecord getFloodRecordBy(IoSession session) {
		SessionManager sessionMgr = SessionManager.INSTANCE;
		FloodRecord record = sessionMgr.getSessionAttr(session, 
				SessionProperties.FLOOD, FloodRecord.class);
		if (record == null) {
			record = new FloodRecord();
			session.setAttribute(SessionProperties.FLOOD, record);
		}
		
		return record;
	}
	
	private static void tryToResetFloodTimes(long now, FloodRecord record) {
		FireWallConfig config = FireWallConfig.getInstance();
		long diffTime = now - record.getLastFloodTime();
		if (NumberUtil.intValue(diffTime/TimeUtil.ONE_SECOND) > config.getFloodWindowSeconds()) {
			record.setFloodTimes(0);
		}
	}
	
	private static boolean isMessageTooFast(int packageSum) {
		FireWallConfig config = FireWallConfig.getInstance();
		return packageSum >= config.getMaxPackagePerSecond();
	}
	
	private static boolean isMeetFloodStandard(int floodTimes) {
		FireWallConfig config = FireWallConfig.getInstance();
		return floodTimes > config.getMaxFloodTimes();
	}

}
