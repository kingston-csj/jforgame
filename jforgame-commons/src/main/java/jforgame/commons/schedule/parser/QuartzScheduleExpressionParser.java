package jforgame.commons.schedule.parser;

import org.quartz.CronExpression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * Cron expression parser based on quartz
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

        // Get the current time as the starting point
        Date now = new Date();

        // Get the first trigger time
        Date firstFireTime = cron.getNextValidTimeAfter(now);
        if (firstFireTime == null) {
            // No trigger time (invalid expression or expired)
            return false;
        }
        // Get the second trigger time
        Date secondFireTime = cron.getNextValidTimeAfter(firstFireTime);

        // If there is a second trigger time, it is periodic
        return secondFireTime != null;
    }


}
