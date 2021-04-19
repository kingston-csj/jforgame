package jforgame.server.game.cron;

import jforgame.common.utils.NumberUtil;
import jforgame.server.game.database.server.ServerRecordPool;
import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public abstract class CronParser {

    private static Logger logger = LoggerFactory.getLogger(CronParser.class.getName());

    /**
     * 解析正宗的cron表达式
     */
    static final CronParser normalParser = new CronParser() {
        @Override
        Date parse(String cron, Date start) {
            try {
                return new CronExpression(cron).getNextValidTimeAfter(start);
            } catch (ParseException e) {
                logger.error("", e);
                return null;
            }
        }
    };

    /**
     * 解析开服时间的cron表达式
     */
    static final CronParser openServerParser = new CronParser() {
        @Override
        Date parse(String cron, Date start) {
            Date openServerDate = ServerRecordPool.INSTANCE.getOpenServerDate();
            if (openServerDate == null) {
                return null;
            }
            // 59 59 12 1 *
            String[] splits = cron.split(" ");
            Calendar next = Calendar.getInstance();
            next.setTime(openServerDate);
            next.add(Calendar.DAY_OF_YEAR, NumberUtil.intValue(splits[3]));
            next.add(Calendar.HOUR_OF_DAY, NumberUtil.intValue(splits[2]));
            next.add(Calendar.MINUTE, NumberUtil.intValue(splits[1]));
            next.add(Calendar.SECOND, NumberUtil.intValue(splits[0]));
            return next.getTime();
        }
    };

    /**
     * 解析合服时间的cron表达式
     */
    static final CronParser mergedServerParser = new CronParser() {
        @Override
        Date parse(String cron, Date start) {
            Date openServerDate = ServerRecordPool.INSTANCE.getMergedServerDate();
            if (openServerDate == null) {
                return null;
            }
            // 59 59 12 1 * *
            String[] splits = cron.split(" ");
            Calendar next = Calendar.getInstance();
            next.setTime(openServerDate);
            next.add(Calendar.DAY_OF_YEAR, NumberUtil.intValue(splits[3]));
            next.add(Calendar.HOUR_OF_DAY, NumberUtil.intValue(splits[2]));
            next.add(Calendar.MINUTE, NumberUtil.intValue(splits[1]));
            next.add(Calendar.SECOND, NumberUtil.intValue(splits[0]));
            return next.getTime();
        }
    };

    abstract Date parse(String cron, Date start);

    public static Date getTime(String cron, Date start) {
        // 开服表达式
        if (cron.split(" ").length == 5) {
            return openServerParser.parse(cron, start);
        }
        // 合服表达式
        else if (cron.split(" ").length == 6) {
            return mergedServerParser.parse(cron, start);
        }
        return new Date();
    }

    public static void main(String[] args) {
        System.out.println(CronParser.getTime("59 59 12 1 *", new Date()));
    }
}
