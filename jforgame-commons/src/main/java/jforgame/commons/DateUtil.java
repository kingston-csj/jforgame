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
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {
                // 尝试解析为 LocalDate
                LocalDate localDate = LocalDate.parse(dateString.trim(), formatter);
                return java.util.Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            } catch (DateTimeParseException e1) {
                try {
                    // 若解析为 LocalDate 失败，尝试解析为 LocalDateTime
                    LocalDateTime localDateTime = LocalDateTime.parse(dateString.trim(), formatter);
                    return java.util.Date.from(localDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());
                } catch (DateTimeParseException e2) {
                    // 若当前格式解析失败，尝试下一个格式
                }
            }
        }
        // 若所有格式都无法解析，返回 null
        return null;
    }

    /**
     * tell if two timestamps are on the same day
     *
     * @param t1 date1
     * @param t2 date2
     * @return true if they are on the same day
     */
    public static boolean isSameDay(Date t1, Date t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(t1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(t2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean isSameDay(long t1, long t2) {
        return isSameDay(new Date(t1), new Date(t2));
    }

    /**
     * tell if two timestamps are in the same week
     * warming: the first day of the week is Monday
     *
     * @param t1 date1
     * @param t2 date2
     * @return true if they are in the same week
     */
    public static boolean isSameWeek(Date t1, Date t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        // 获取 Calendar 实例
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        // 设置 Calendar 的时间
        cal1.setTime(t1);
        cal2.setTime(t2);
        // 设置每周的第一天为周一
        cal1.setFirstDayOfWeek(Calendar.MONDAY);
        cal2.setFirstDayOfWeek(Calendar.MONDAY);
        // 设置判断一周的最小天数，这里设置为 4 天
        cal1.setMinimalDaysInFirstWeek(4);
        cal2.setMinimalDaysInFirstWeek(4);

        int week1 = cal1.get(Calendar.WEEK_OF_YEAR);
        int week2 = cal2.get(Calendar.WEEK_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);

        return year1 == year2 && week1 == week2;
    }

}