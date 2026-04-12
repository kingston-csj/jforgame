package jforgame.demo.game.logger;


import jforgame.commons.util.DateUtil;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.logger.LoggerUtil;

import java.util.Date;

public class LoggerUtils {

    /**
     * Log an exception at the ERROR level with an accompanying message.
     *
     * @param errMsg the message accompanying the exception
     * @param e      the exception to log
     */
    public static void error(String errMsg, Exception e) {
        LoggerUtil.error(errMsg, e);
    }

    public static void error(String format, Object... arguments) {
        LoggerUtil.error(format, arguments);
    }

    public static void info(LoggerBusiness logger, Object... args) {
        Object[] newArgs = new Object[args.length + 4];

        // 写入拓展的固定字段（放在最前面）
        newArgs[0] = "time";
        newArgs[1] = System.currentTimeMillis();
        newArgs[2] = "date";
        newArgs[3] = DateUtil.format(new Date());

        System.arraycopy(args, 0, newArgs, 4, args.length);
        // 调用真正的日志输出
        LoggerUtil.info(logger.name(), newArgs);
    }

    public static void logPlayer(LoggerBusiness logger, PlayerEnt player, Object... args) {
        // 拓展args参数
        Object[] newArgs = new Object[args.length + 8];
        newArgs[0] = "time";
        newArgs[1] = System.currentTimeMillis();
        newArgs[2] = "date";
        newArgs[3] = DateUtil.format(new Date());
        newArgs[4] = "playerId";
        newArgs[5] = player.getId();
        System.arraycopy(args, 0, newArgs, 8, args.length);
        LoggerUtil.info(logger.name(), newArgs);
    }
}