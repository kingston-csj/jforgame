package jforgame.commons.util;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link DateUtil}
 */
public class DateUtilTest {

    @Test
    public void testAddDays() {
        Date date = new Date();
        Date nextDay = DateUtil.addDays(date, 1);
        assertTrue(nextDay.after(date));
    }

    @Test
    public void testAddDaysNegative() {
        Date date = new Date();
        Date prevDay = DateUtil.addDays(date, -1);
        assertTrue(prevDay.before(date));
    }

    @Test
    public void testAddHours() {
        Date date = new Date();
        Date newDate = DateUtil.addHours(date, 2);
        assertTrue(newDate.after(date));
    }

    @Test
    public void testAddMinutes() {
        Date date = new Date();
        Date newDate = DateUtil.addMinutes(date, 30);
        assertTrue(newDate.after(date));
    }

    @Test
    public void testAddSeconds() {
        Date date = new Date();
        Date newDate = DateUtil.addSeconds(date, 60);
        assertTrue(newDate.after(date));
    }

    @Test
    public void testAddMonths() {
        Date date = new Date();
        Date newDate = DateUtil.addMonths(date, 1);
        assertTrue(newDate.after(date));
    }

    @Test
    public void testAddWeeks() {
        Date date = new Date();
        Date newDate = DateUtil.addWeeks(date, 1);
        assertTrue(newDate.after(date));
    }

    @Test
    public void testAddMilliseconds() {
        Date date = new Date();
        Date newDate = DateUtil.addMilliseconds(date, 1000);
        assertTrue(newDate.after(date));
    }

    @Test
    public void testGetWeekDay() {
        int weekDay = DateUtil.getWeekDay();
        assertTrue(weekDay >= 1 && weekDay <= 7);
    }

    @Test
    public void testGetChinaWeekDay() {
        int weekDay = DateUtil.getChinaWeekDay();
        assertTrue(weekDay >= 1 && weekDay <= 7);
    }

    @Test
    public void testGetDiffDays() {
        Date date1 = new Date();
        Date date2 = DateUtil.addDays(date1, 5);
        int diff = DateUtil.getDiffDays(date1, date2);
        assertEquals(5, diff);
    }

    @Test
    public void testGetDiffDaysNegative() {
        Date date1 = new Date();
        Date date2 = DateUtil.addDays(date1, -3);
        int diff = DateUtil.getDiffDays(date1, date2);
        assertEquals(-3, diff);
    }

    @Test(expected = NullPointerException.class)
    public void testGetDiffDaysNull() {
        DateUtil.getDiffDays(null, new Date());
    }

    @Test
    public void testParseDate() {
        Date result = DateUtil.parseDate("2024-01-15");
        assertNotNull(result);
    }

    @Test
    public void testParseDateWithTime() {
        Date result = DateUtil.parseDate("2024-01-15 10:30:00");
        assertNotNull(result);
    }

    @Test
    public void testParseDateNull() {
        assertNull(DateUtil.parseDate(null));
    }

    @Test
    public void testParseDateEmpty() {
        assertNull(DateUtil.parseDate(""));
    }

    @Test
    public void testParseDateInvalid() {
        assertNull(DateUtil.parseDate("invalid-date"));
    }

    @Test
    public void testIsSameDay() {
        Date date1 = new Date();
        Date date2 = new Date();
        assertTrue(DateUtil.isSameDay(date1, date2));
    }

    @Test
    public void testIsSameDayDifferent() {
        Date date1 = new Date();
        Date date2 = DateUtil.addDays(date1, 1);
        assertFalse(DateUtil.isSameDay(date1, date2));
    }

    @Test
    public void testIsSameDayLong() {
        long time1 = System.currentTimeMillis();
        long time2 = System.currentTimeMillis();
        assertTrue(DateUtil.isSameDay(time1, time2));
    }

    @Test
    public void testIsSameDayNull() {
        assertFalse(DateUtil.isSameDay((Date) null, new Date()));
        assertFalse(DateUtil.isSameDay(new Date(), null));
        assertFalse(DateUtil.isSameDay((Date) null, (Date) null));
    }

    @Test
    public void testIsSameWeek() {
        Date date1 = new Date();
        Date date2 = new Date();
        assertTrue(DateUtil.isSameWeek(date1, date2));
    }

    @Test
    public void testIsSameWeekNull() {
        assertFalse(DateUtil.isSameWeek(null, new Date()));
        assertFalse(DateUtil.isSameWeek(new Date(), null));
    }

    @Test
    public void testIsToday() {
        assertTrue(DateUtil.isToday(System.currentTimeMillis()));
    }

    @Test
    public void testIsTodayYesterday() {
        long yesterday = System.currentTimeMillis() - TimeUtil.MILLIS_PER_DAY;
        assertFalse(DateUtil.isToday(yesterday));
    }

    @Test
    public void testGetDayDiffFromToday() {
        long today = System.currentTimeMillis();
        assertEquals(1, DateUtil.getDayDiffFromToday(today));
    }

    @Test
    public void testGetDayDiffFromTodayYesterday() {
        long yesterday = today() - TimeUtil.MILLIS_PER_DAY;
        assertEquals(2, DateUtil.getDayDiffFromToday(yesterday));
    }

    @Test
    public void testGetDayDiffFromTodayFuture() {
        long future = today() + TimeUtil.MILLIS_PER_DAY;
        assertEquals(-1, DateUtil.getDayDiffFromToday(future));
    }

    private long today() {
        return System.currentTimeMillis();
    }
}
