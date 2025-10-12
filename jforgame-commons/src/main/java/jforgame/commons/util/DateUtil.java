package jforgame.commons.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * 日期工具类
 */
public final class DateUtil {

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
     * 向指定日期添加指定的时间量
     *
     * @param date          要添加时间的日期对象，不能为空
     * @param calendarField 要添加的时间字段，如Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY 等
     * @param amount        要添加的时间量，可能为负数
     * @return 新的日期对象，包含添加的时间量
     */
    private static Date add(final Date date, final int calendarField, final int amount) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * 向指定日期添加指定的天数
     *
     * @param date   要添加天的日期对象，不能为空
     * @param amount 要添加的天数，可能为负数
     * @return 新的日期对象，包含添加的天数
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * 向指定日期添加指定的小时数
     *
     * @param date   要添加小时的日期对象，不能为空
     * @param amount 要添加的小时数，可能为负数
     * @return 新的日期对象，包含添加的小时数
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * 向指定日期添加指定的毫秒数
     *
     * @param date   要添加毫秒的日期对象，不能为空
     * @param amount 要添加的毫秒数，可能为负数
     * @return 新的日期对象，包含添加的毫秒数
     */
    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    /**
     * 向指定日期添加指定的分钟数
     *
     * @param date   要添加分钟的日期对象，不能为空
     * @param amount 要添加的分钟数，可能为负数
     * @return 新的日期对象，包含添加的分钟数
     */
    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * 向指定日期添加指定的月份数
     *
     * @param date   要添加月份的日期对象，不能为空
     * @param amount 要添加的月份数，可能为负数
     * @return 新的日期对象，包含添加的月份数
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * 向指定日期添加指定的秒数
     *
     * @param date   要添加秒的日期对象，不能为空
     * @param amount 要添加的秒数，可能为负数
     * @return 新的日期对象，包含添加的秒数
     */
    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * 向指定日期添加指定的周数
     *
     * @param date   要添加周的日期对象，不能为空
     * @param amount 要添加的周数，可能为负数
     * @return 新的日期对象，包含添加的周数
     */
    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * 获取中国传统的周几，其中星期一为第一天，星期日为第七天
     *
     * @return 中国传统的周几，范围为1到7
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
     * 获取两个日期之间的天数差
     *
     * @param startDate 开始日期，不能为空
     * @param endDate   结束日期，不能为空
     * @return 两个日期之间的天数差，可能为负数
     */
    public static int getDiffDays(Date startDate, Date endDate) {
        Objects.requireNonNull(startDate);
        Objects.requireNonNull(endDate);
        long start = startDate.getTime();
        long end = endDate.getTime();
        return (int) ((end - start) / TimeUtil.MILLIS_PER_DAY);
    }

    /**
     * 获取指定日期的周几，其中星期一为第一天，星期日为第七天
     *
     * @return 周几，范围为1到7
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
     * 解析日期字符串为 Date 对象
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
                LocalDateTime localDateTime = LocalDateTime.parse(dateString, formatter);
                return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            } catch (Exception e1) {
                try {
                    LocalDate localDate = LocalDate.parse(dateString, formatter);
                    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                } catch (Exception e2) {
                    // 继续尝试下一个格式
                }
            }
        }
        // 若所有格式都无法解析，返回 null
        return null;
    }

    /**
     * 检查两个时间戳是否在同一天
     *
     * @param t1 时间戳1
     * @param t2 时间戳2
     * @return 如果它们在同一天则返回 true，否则返回 false
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

    /**
     * 检查两个时间戳是否在同一天
     *
     * @param t1 时间戳1
     * @param t2 时间戳2
     * @return 如果它们在同一天则返回 true，否则返回 false
     */
    public static boolean isSameDay(long t1, long t2) {
        return isSameDay(new Date(t1), new Date(t2));
    }

    /**
     * 检查两个时间戳是否在同一周
     * 警告：每周的第一天被视为周一
     *
     * @param t1 时间戳1
     * @param t2 时间戳2
     * @return 如果它们在同一周则返回 true，否则返回 false
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
    /**
     * 检查指定时间戳是否为今天
     *
     * @param time 时间戳
     * @return 如果是今天则返回 true，否则返回 false
     */
    public static boolean isToday(long time) {
        Instant instant = Instant.ofEpochMilli(time);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate date = instant.atZone(zoneId).toLocalDate();
        LocalDate today = LocalDate.now();
        return date.isEqual(today);
    }

}