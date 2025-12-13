package jforgame.commons.schedule.parser;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * 基于quartz的cron表达式解析器
 */
public class QuartzScheduleExpressionParser implements ScheduleExpressionParser {

    Logger logger = LoggerFactory.getLogger("ExpressionParser");

    @Override
    public boolean isValidExpression(String expression) {
        try {
            new CronExpression(expression);
            return true;
        } catch (ParseException var2) {
            return false;
        }
    }

    @Override
    public Date getNextTriggerTimeAfter(String expression, Date start) {
        try {
            return new CronExpression(expression).getNextValidTimeAfter(start);
        } catch (ParseException e) {
            logger.error("parse {} failed", expression, e);
            return null;
        }
    }

    @Override
    public boolean isPeriodicExpression(String expression) {
        CronExpression cron = null;
        try {
            cron = new CronExpression(expression);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        cron.setTimeZone(TimeZone.getDefault());

        // 获取当前时间作为起始点
        Date now = new Date();

        // 获取第一个触发时间
        Date firstFireTime = cron.getNextValidTimeAfter(now);
        if (firstFireTime == null) {
            // 没有触发时间（无效表达式或已过期）
            return false;
        }
        // 获取第二个触发时间
        Date secondFireTime = cron.getNextValidTimeAfter(firstFireTime);

        // 如果有第二个触发时间，则为周期性
        return secondFireTime != null;
    }


}
