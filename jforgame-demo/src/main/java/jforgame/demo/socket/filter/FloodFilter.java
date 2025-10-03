package jforgame.demo.socket.filter;

import jforgame.commons.util.NumberUtil;
import jforgame.commons.util.TimeUtil;
import jforgame.demo.FireWall;
import jforgame.demo.ServerConfig;
import jforgame.socket.share.MessageHandler;
import jforgame.demo.socket.model.FloodRecord;
import jforgame.socket.share.IdSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 流量洪水过滤器
 */
public class FloodFilter implements MessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(FloodFilter.class);

    private static final String KEY_FLOOD = "FLOOD";

    @Override
    public boolean messageReceived(IdSession session, Object message)
            throws Exception {
        FloodRecord record = getFloodRecordBy(session);

        // 基础思路：玩家XX秒内累计洪水记录超过Y次，则判定为嫌疑人
        long now = System.currentTimeMillis();
        long currSecond = NumberUtil.intValue(now / TimeUtil.MILLIS_PER_SECOND);
        long lastTime = record.getLastReceivedTime();
        int lastSecond = NumberUtil.intValue(lastTime / TimeUtil.MILLIS_PER_SECOND);

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

        return true;
    }

    private static FloodRecord getFloodRecordBy(IdSession session) {
        Object record = session.getAttribute(
                KEY_FLOOD);
        if (record == null) {
            record = new FloodRecord();
            session.setAttribute(KEY_FLOOD, record);
        }

        return (FloodRecord) record;
    }

    private static void tryToResetFloodTimes(long now, FloodRecord record) {
        FireWall config = ServerConfig.getInstance().getFireWall();
        long diffTime = now - record.getLastFloodTime();
        if (NumberUtil.intValue(diffTime / TimeUtil.MILLIS_PER_SECOND) > config.getFloodWindowSeconds()) {
            record.setFloodTimes(0);
        }
    }

    private static boolean isMessageTooFast(int packageSum) {
        FireWall config = ServerConfig.getInstance().getFireWall();
        return packageSum >= config.getMaxPackagePerSecond();
    }

    private static boolean isMeetFloodStandard(int floodTimes) {
        FireWall config = ServerConfig.getInstance().getFireWall();
        return floodTimes > config.getMaxFloodTimes();
    }

}
