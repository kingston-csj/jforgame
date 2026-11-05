package jforgame.commons.schedule.parser;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ScheduleExpressionParserManager}
 */
public class ScheduleExpressionParserManagerTest {

    @Before
    public void setUp() {
        // Reset by creating a default state (we can't directly clear the static list)
        // But we can test with the default parser
    }

    @Test
    public void testGetNextTriggerTimeDefault() {
        // Default parser should handle cron expressions
        Date now = new Date();
        Date next = ScheduleExpressionParserManager.getNextTriggerTimeAfter("0 * * * * ?", now);
        assertNotNull(next);
        assertTrue(next.after(now));
    }

    @Test
    public void testGetNextTriggerTimeInvalid() {
        Date now = new Date();
        Date next = ScheduleExpressionParserManager.getNextTriggerTimeAfter("invalid", now);
        assertNull(next);
    }

    @Test
    public void testGetParser() {
        // Default parser should handle cron expressions
        ScheduleExpressionParser parser = ScheduleExpressionParserManager.getParser("0 * * * * ?");
        assertNotNull(parser);
        assertTrue(parser instanceof QuartzScheduleExpressionParser);
    }

    @Test
    public void testGetParserInvalid() {
        ScheduleExpressionParser parser = ScheduleExpressionParserManager.getParser("invalid");
        assertNull(parser);
    }

    @Test
    public void testAddParser() {
        CustomParser customParser = new CustomParser();
        ScheduleExpressionParserManager.addParserAfter(customParser);

        // Custom parser should be found for its expression
        ScheduleExpressionParser found = ScheduleExpressionParserManager.getParser("CUSTOM:test");
        assertEquals(customParser, found);
    }

    // Custom parser for testing
    private static class CustomParser implements ScheduleExpressionParser {
        @Override
        public boolean isValidExpression(String expression) {
            return expression != null && expression.startsWith("CUSTOM:");
        }

        @Override
        public Date getNextTriggerTimeAfter(String expression, Date date) {
            return new Date(date.getTime() + 1000);
        }

        @Override
        public boolean isPeriodicExpression(String expression) {
            return true;
        }
    }
}
