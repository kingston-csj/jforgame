package jforgame.commons.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link TimeUtil}
 */
public class TimeUtilTest {

    @Test
    public void testConstants() {
        assertEquals(1000L, TimeUtil.MILLIS_PER_SECOND);
        assertEquals(60000L, TimeUtil.MILLIS_PER_MINUTE);
        assertEquals(3600000L, TimeUtil.MILLIS_PER_HOUR);
        assertEquals(86400000L, TimeUtil.MILLIS_PER_DAY);
        assertEquals(604800000L, TimeUtil.MILLIS_PER_WEEK);
    }

    @Test
    public void testConstantsRelationships() {
        assertEquals(TimeUtil.MILLIS_PER_SECOND, 1000);
        assertEquals(TimeUtil.MILLIS_PER_MINUTE, TimeUtil.MILLIS_PER_SECOND * 60);
        assertEquals(TimeUtil.MILLIS_PER_HOUR, TimeUtil.MILLIS_PER_MINUTE * 60);
        assertEquals(TimeUtil.MILLIS_PER_DAY, TimeUtil.MILLIS_PER_HOUR * 24);
        assertEquals(TimeUtil.MILLIS_PER_WEEK, TimeUtil.MILLIS_PER_DAY * 7);
    }

    @Test
    public void testGetYear() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 10, 30, 0);
        Date date = cal.getTime();

        int year = TimeUtil.getYear(date.getTime());
        assertEquals(2024, year);
    }

    @Test
    public void testGetMonth() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JUNE, 15, 10, 30, 0);
        Date date = cal.getTime();

        int month = TimeUtil.getMonth(date.getTime());
        assertEquals(6, month); // June
    }

    @Test
    public void testGetDay() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.JANUARY, 15, 10, 30, 0);
        Date date = cal.getTime();

        int day = TimeUtil.getDay(date.getTime());
        assertEquals(15, day);
    }

    @Test
    public void testGetYearCurrent() {
        Calendar cal = Calendar.getInstance();
        int expectedYear = cal.get(Calendar.YEAR);
        int year = TimeUtil.getYear(System.currentTimeMillis());
        assertEquals(expectedYear, year);
    }

    @Test
    public void testGetMonthCurrent() {
        Calendar cal = Calendar.getInstance();
        int expectedMonth = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is 0-based
        int month = TimeUtil.getMonth(System.currentTimeMillis());
        assertEquals(expectedMonth, month);
    }

    @Test
    public void testGetDayCurrent() {
        Calendar cal = Calendar.getInstance();
        int expectedDay = cal.get(Calendar.DAY_OF_MONTH);
        int day = TimeUtil.getDay(System.currentTimeMillis());
        assertEquals(expectedDay, day);
    }
}
