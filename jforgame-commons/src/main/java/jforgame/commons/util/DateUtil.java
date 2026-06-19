package jforgame.commons.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

/**
 * Date utility class
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
     * Adds a specified amount of time to the given date
     *
     * @param date          the date to add time to, must not be null
     * @param calendarField the time field to add, such as Calendar.DAY_OF_MONTH, Calendar.HOUR_OF_DAY, etc.
     * @param amount        the amount of time to add, may be negative
     * @return a new date object with the added time amount
     */
    private static Date add(final Date date, final int calendarField, final int amount) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    /**
     * Adds a specified number of days to the given date
     *
     * @param date   the date to add days to, must not be null
     * @param amount the number of days to add, may be negative
     * @return a new date object with the added days
     */
    public static Date addDays(final Date date, final int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * Adds a specified number of hours to the given date
     *
     * @param date   the date to add hours to, must not be null
     * @param amount the number of hours to add, may be negative
     * @return a new date object with the added hours
     */
    public static Date addHours(final Date date, final int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    /**
     * Adds a specified number of milliseconds to the given date
     *
     * @param date   the date to add milliseconds to, must not be null
     * @param amount the number of milliseconds to add, may be negative
     * @return a new date object with the added milliseconds
     */
    public static Date addMilliseconds(final Date date, final int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    /**
     * Adds a specified number of minutes to the given date
     *
     * @param date   the date to add minutes to, must not be null
     * @param amount the number of minutes to add, may be negative
     * @return a new date object with the added minutes
     */
    public static Date addMinutes(final Date date, final int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    /**
     * Adds a specified number of months to the given date
     *
     * @param date   the date to add months to, must not be null
     * @param amount the number of months to add, may be negative
     * @return a new date object with the added months
     */
    public static Date addMonths(final Date date, final int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * Adds a specified number of seconds to the given date
     *
     * @param date   the date to add seconds to, must not be null
     * @param amount the number of seconds to add, may be negative
     * @return a new date object with the added seconds
     */
    public static Date addSeconds(final Date date, final int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    /**
     * Adds a specified number of weeks to the given date
     *
     * @param date   the date to add weeks to, must not be null
     * @param amount the number of weeks to add, may be negative
     * @return a new date object with the added weeks
     */
    public static Date addWeeks(final Date date, final int amount) {
        return add(date, Calendar.WEEK_OF_YEAR, amount);
    }

    /**
     * Gets the Chinese traditional day of week, where Monday is the first day and Sunday is the seventh day
     *
     * @return the Chinese traditional day of week, ranging from 1 to 7
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
     * Gets the number of days between two dates
     *
     * @param startDate the start date, must not be null
     * @param endDate   the end date, must not be null
     * @return the number of days between the two dates, may be negative
     */
    public static int getDiffDays(Date startDate, Date endDate) {
        Objects.requireNonNull(startDate);
        Objects.requireNonNull(endDate);
        long start = startDate.getTime();
        long end = endDate.getTime();
        return (int) ((end - start) / TimeUtil.MILLIS_PER_DAY);
    }

    /**
     * Gets the day of week for the specified date, where Monday is the first day and Sunday is the seventh day
     *
     * @return the day of week, ranging from 1 to 7
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
     * Parses a date string into a Date object
     * The date format can be yyyy-MM-dd or yyyy-MM-dd HH:mm:ss
     *
     * @param dateString the date string
     * @return the parsed Date object if successful, otherwise null
     */
    public static Date parseDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        dateString = dateString.trim();
        if (dateString.isEmpty()) {
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
                    // Continue to try the next format
                }
            }
        }
        // If all formats fail to parse, return null
        return null;
    }

    /**
     * Checks if two timestamps are on the same day
     *
     * @param t1 timestamp 1
     * @param t2 timestamp 2
     * @return true if they are on the same day, otherwise false
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
     * Checks if two timestamps are on the same day
     *
     * @param t1 timestamp 1
     * @param t2 timestamp 2
     * @return true if they are on the same day, otherwise false
     */
    public static boolean isSameDay(long t1, long t2) {
        return isSameDay(new Date(t1), new Date(t2));
    }

    /**
     * Checks if two timestamps are in the same week
     * Warning: The first day of each week is considered as Monday
     *
     * @param t1 timestamp 1
     * @param t2 timestamp 2
     * @return true if they are in the same week, otherwise false
     */
    public static boolean isSameWeek(Date t1, Date t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        // Get Calendar instances
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        // Set Calendar time
        cal1.setTime(t1);
        cal2.setTime(t2);
        // Set the first day of each week as Monday
        cal1.setFirstDayOfWeek(Calendar.MONDAY);
        cal2.setFirstDayOfWeek(Calendar.MONDAY);
        // Set the minimum number of days in the first week, here set to 4 days
        cal1.setMinimalDaysInFirstWeek(4);
        cal2.setMinimalDaysInFirstWeek(4);

        int week1 = cal1.get(Calendar.WEEK_OF_YEAR);
        int week2 = cal2.get(Calendar.WEEK_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);

        return year1 == year2 && week1 == week2;
    }

    /**
     * Checks if the specified timestamp is today
     *
     * @param time the timestamp
     * @return true if it is today, otherwise false
     */
    public static boolean isToday(long time) {
        Instant instant = Instant.ofEpochMilli(time);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate date = instant.atZone(zoneId).toLocalDate();
        LocalDate today = LocalDate.now();
        return date.isEqual(today);
    }

    /**
     * Calculates the number of days difference between the specified timestamp and today
     * If it is today, returns 1; yesterday = 2; two days ago = 3...
     * Note: If the specified timestamp is greater than the current timestamp, returns -1!
     *
     * @param timestamp the timestamp in milliseconds
     * @return today = 1, yesterday = 2, two days ago = 3...; future time returns -1
     */
    public static int getDayDiffFromToday(long timestamp) {
        ZoneId zoneId = ZoneId.systemDefault();
        // Today's date
        LocalDate today = LocalDate.now(zoneId);

        LocalDate targetDate = new Date(timestamp)
                .toInstant()
                .atZone(zoneId)
                .toLocalDate();

        // Future time returns -1
        if (targetDate.isAfter(today)) {
            return -1;
        }
        // Calculate the number of days difference
        long days = ChronoUnit.DAYS.between(targetDate, today);
        // Rule: today = 1, yesterday = 2...
        return (int) days + 1;
    }

}