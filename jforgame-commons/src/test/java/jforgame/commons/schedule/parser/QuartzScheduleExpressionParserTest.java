package jforgame.commons.schedule.parser;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link QuartzScheduleExpressionParser}
 */
public class QuartzScheduleExpressionParserTest {

    private final QuartzScheduleExpressionParser parser = new QuartzScheduleExpressionParser();

    @Test
    public void testIsValidCronExpression() {
        assertTrue(parser.isValidExpression("0 * * * * ?"));
        assertTrue(parser.isValidExpression("0 0 12 * * ?"));
        assertTrue(parser.isValidExpression("0 0 12 ? * MON"));
        assertTrue(parser.isValidExpression("0 0/5 14 * * ?"));
    }

    @Test
    public void testIsInvalidCronExpression() {
        assertFalse(parser.isValidExpression("invalid expression"));
        assertFalse(parser.isValidExpression(""));
        assertFalse(parser.isValidExpression("* * *"));
    }

    @Test
    public void testGetNextTriggerTime() {
        Date now = new Date();
        // Cron for every second
        Date next = parser.getNextTriggerTimeAfter("0 * * * * ?", now);
        assertNotNull(next);
        assertTrue(next.after(now));
    }

    @Test
    public void testGetNextTriggerTimeInvalidExpression() {
        Date now = new Date();
        Date next = parser.getNextTriggerTimeAfter("invalid", now);
        assertNull(next);
    }

    @Test
    public void testIsPeriodicExpression() {
        assertTrue(parser.isPeriodicExpression("0 * * * * ?"));
        assertTrue(parser.isPeriodicExpression("0 0 12 * * ?"));
    }

    @Test(expected = RuntimeException.class)
    public void testIsPeriodicExpressionInvalid() {
        parser.isPeriodicExpression("invalid");
    }
}
