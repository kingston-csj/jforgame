package jforgame.commons.util;

import java.text.SimpleDateFormat;

/**
 * 时间工具类
 */
public final class TimeUtil {


    private TimeUtil() {

    }

    /**
     * Number of milliseconds in a standard second.
     */
    public static final long MILLIS_PER_SECOND = 1000;
    /**
     * Number of milliseconds in a standard minute.
     */
    public static final long MILLIS_PER_MINUTE = 60 * MILLIS_PER_SECOND;

    /**
     * Number of milliseconds in a standard hour.
     *
     * @since 2.1
     */
    public static final long MILLIS_PER_HOUR = 60 * MILLIS_PER_MINUTE;

    /**
     * Number of milliseconds in a standard day.
     */
    public static final long MILLIS_PER_DAY = 24 * MILLIS_PER_HOUR;

    /**
     * Number of milliseconds in a standard week.
     */
    public static final long MILLIS_PER_WEEK = 7 * MILLIS_PER_DAY;


    public static int getYear(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] split = format.format(time).split("-");
        return NumberUtil.intValue(split[0]);
    }

    public static int getMonth(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] split = format.format(time).split("-");
        return NumberUtil.intValue(split[1]);
    }

    public static int getDay(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String[] split = format.format(time).split("-");
        return NumberUtil.intValue(split[2]);
    }

}
