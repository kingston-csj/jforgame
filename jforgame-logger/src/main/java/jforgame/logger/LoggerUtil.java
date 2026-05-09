package jforgame.logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 日志工具类
 * 对使用{@link Logger#error(String)}记录的单行日志，会重定向输出到exception目录，该日志不带堆栈信息。
 * 对使用{@link #info}记录的日志，视为运营日志，会根据日志类型输出到对应的目录。所有参数会被格式化键值对，通过'|'进行分隔，方便第三方解析。
 * 对使用{@link #error(String, Exception)}记录的日志，会重定向输出到exception目录，该日志会带堆栈信息。
 */
public class LoggerUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerUtil.class);

    /**
     * Log an exception at the ERROR level with an accompanying message.
     *
     * @param errMsg the message accompanying the exception
     * @param e      the exception to log
     */
    public static void error(String errMsg, Exception e) {
        LOGGER.error(errMsg, e);
    }

    public static void error(String format, Object... arguments) {
        LOGGER.error(format, arguments);
    }

    /**
     * 根据logger自动对日志类型进行分类，每种类型一个独立目录
     *
     * @param logger 日志类型
     * @param args   参数，个数必须是偶数
     */
    public static void info(String logger, Object... args) {
        logInternal(logger, args);
    }

    private static void logInternal(String loggerName, Object... args) {
        if (args.length == 0) {
            return;
        }
        if (args.length % 2 != 0) {
            throw new IllegalArgumentException("args.length must be even");
        }
        AppLogger logger = LoggerBuilder.getLogger(loggerName);
        StringBuilder sb = new StringBuilder();

        for (int i = 0, n = args.length; i < n; i += 2) {
            String key = String.valueOf(args[i]);
            Object value = args[i + 1];
            sb.append(key).append("|")
                    .append(value).append("|");
        }
        sb.deleteCharAt(sb.length() - 1);
        logger.info(sb.toString());
    }

}
