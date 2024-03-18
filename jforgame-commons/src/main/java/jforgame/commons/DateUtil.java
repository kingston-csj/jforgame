package jforgame.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 */
public class DateUtil {

    private static final ThreadLocal<SimpleDateFormat> STANDARD_FMT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static String format(Date date) {
        return STANDARD_FMT.get().format(date);
    }


    /**
     * Adds to a date 
     *
     * @param date  the date, not null
     * @param calendarField  the calendar field to add to
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    private static Date add(final Date date, final int calendarField, final int amount) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * Adds days to a date
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * Adds hours to a date
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * Adds milliseconds to a date
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    /**
     * Adds minutes to a date
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * Adds months to a date
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * Adds seconds to a date
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * Adds weeks to a date
     *
     * @param date  the date, not null
     * @param amount  the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

}