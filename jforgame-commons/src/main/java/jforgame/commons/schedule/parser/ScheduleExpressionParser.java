package jforgame.commons.schedule.parser;

import java.util.Date;

/**
 * Time expression parser
 * Background: Neither Spring expressions nor cron expressions support custom format expressions.
 * Using this class, you can extend custom format time expressions commonly used in games, such as server opening time expressions, server merging time expressions, etc.
 * Unified management of all one-time/periodic expression-based time parsers for game business.
 * Mainly integrates cron expressions and custom expressions for game business.
 */
public interface ScheduleExpressionParser {

    /**
     * Checks if the expression is a valid format for this parser
     *
     * @param expression the expression to check
     * @return true if valid, false otherwise
     */
    boolean isValidExpression(String expression);

    /**
     * Calculates the next date after the reference time
     *
     * @param expression cron expression
     * @param date       reference date
     * @return the next trigger time
     */
    Date getNextTriggerTimeAfter(String expression, Date date);

    /**
     * Checks if the expression is a periodic expression
     *
     * @param expression cron expression
     * @return true if periodic, false otherwise
     */
    boolean isPeriodicExpression(String expression);
}
