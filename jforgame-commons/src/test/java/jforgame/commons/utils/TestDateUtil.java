package jforgame.commons.utils;

import jforgame.commons.DateUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;

public class TestDateUtil {

    @Test
    public void test() {
        Date date1 = DateUtil.parseDate("2025-02-17 12:00:00");
        Date date2 = DateUtil.parseDate("2025-02-23 12:00:00 ");
        Assert.assertTrue(DateUtil.isSameWeek(date1, date2));

        date1 = DateUtil.parseDate("2025-02-16 12:00:00");
        date2 = DateUtil.parseDate("2025-02-22 12:00:00");
        Assert.assertFalse(DateUtil.isSameWeek(date1, date2));

        date1 = DateUtil.parseDate("2025-02-24 00:00:00");
        date2 = DateUtil.parseDate("2025-03-02 23:59:59");
        Assert.assertTrue(DateUtil.isSameWeek(date1, date2));

        date1 = DateUtil.parseDate("2025-02-24 00:00:00");
        date2 = DateUtil.parseDate("2025-03-03 00:00:01");
        Assert.assertFalse(DateUtil.isSameWeek(date1, date2));
    }
}
