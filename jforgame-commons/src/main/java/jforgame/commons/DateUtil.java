package jforgame.commons;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

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

    private DateUtil() {

    }

    public static String format(Date date) {
        return STANDARD_FMT.get().format(date);
    }


    /**
     * Adds to a date
     *
     * @param date          the date, not null
     * @param calendarField the calendar field to add to
     * @param amount        the amount to add, may be negative
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
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * Adds hours to a date
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * Adds milliseconds to a date
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    /**
     * Adds minutes to a date
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * Adds months to a date
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * Adds seconds to a date
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * Adds weeks to a date
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new {@link Date} with the amount added
     */
    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * get the Chinese customary week, where Monday is the first day and Sunday is the seventh day
     */
    public static int getChinaWeekDay() {
        int standardDay = getWeekDay();
        standardDay--;
        if (standardDay == 0) {
            standardDay = 7;
        }
        return standardDay;
    }

    /**
     * get days between two dates
     */
    public static int getDiffDays(Date startDate, Date endDate) {
        Objects.requireNonNull(startDate);
        Objects.requireNonNull(endDate);
        long start = startDate.getTime();
        long end = endDate.getTime();
        return (int) ((end - start) / TimeUtil.MILLIS_PER_DAY);
    }

    /**
     * get the day of the week in western countries
     */
    public static int getWeekDay() {
        Calendar cal = new GregorianCalendar();
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    private static final List<DateTimeFormatter> FORMATTERS = Arrays.asList(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    );

    /**
     * parse date string to Date object
     * 日期格式可以是 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     *
     * @param dateString 日期字符串
     * @return 解析成功返回 Date 对象，否则返回 null
     */
    public static Date parseDate(String dateString) {
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                // 尝试解析为 LocalDate
                LocalDate localDate = LocalDate.parse(dateString, formatter);
                return java.util.Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e1) {
                try {
                    // 若解析为 LocalDate 失败，尝试解析为 LocalDateTime
                    LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
                    return java.util.Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
                } catch (DateTimeParseException e2) {
                    // 若当前格式解析失败，尝试下一个格式
                }
            }
        }
        // 若所有格式都无法解析，返回 null
        return null;
    }
}