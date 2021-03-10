package jforgame.server.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期工具类
 * @author kinson
 *
 */
public class DateUtil {

    private static ThreadLocal<SimpleDateFormat> sdf = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    public static String format(Date date) {
        return sdf.get().format(date);
    }

    public static void main(String[] args) throws Exception {
        System.err.println(format(new Date()));
    }

}