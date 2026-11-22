package jforgame.demo.game.logger;


import jforgame.commons.util.DateUtil;
import jforgame.commons.util.JsonUtil;
import jforgame.demo.ServerConfig;
import jforgame.logger.LoggerUtil;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class LoggerUtils {

    public static void error(String errMsg, Exception e) {
        LoggerUtil.error(errMsg, e);
    }

    public static void error(String format, Object... arguments) {
        LoggerUtil.error(format, arguments);
    }

    public static void info(LoggerFunction logger, Object... args) {
        if (args.length == 0) {
            return;
        }
        if (args.length % 2 != 0) {
            String errorMsg = String.format("logger %s, args %s", logger, JsonUtil.object2String(args));
            throw new IllegalArgumentException(errorMsg);
        }

        Map<String, Object> root = new LinkedHashMap<>();
        root.put("time", System.currentTimeMillis());
        root.put("date", DateUtil.format(new Date()));
        root.put("sid", ServerConfig.getInstance().getServerId());

        // 业务参数全部塞进 ext 子对象
        Map<String, Object> ext = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            String key = String.valueOf(args[i]);
            Object val = args[i + 1];
            ext.put(key, val);
        }
        root.put("ext", ext);

        String json = JsonUtil.object2String(root);
        LoggerUtil.info(logger.name(), json);
    }

    public static void logPlayer(LoggerFunction logger, String playerId, Object... args) {
        if (args.length == 0) {
            return;
        }
        if (args.length % 2 != 0) {
            String errorMsg = String.format("logger %s, args %s", logger, JsonUtil.object2String(args));
            throw new IllegalArgumentException(errorMsg);
        }
        Map<String, Object> root = new LinkedHashMap<>();
        root.put("time", System.currentTimeMillis());
        root.put("date", DateUtil.format(new Date()));
        root.put("sid", ServerConfig.getInstance().getServerId());
        root.put("playerId", playerId);
        Map<String, Object> ext = new LinkedHashMap<>();
        for (int i = 0; i < args.length; i += 2) {
            String key = String.valueOf(args[i]);
            Object val = args[i + 1];
            ext.put(key, val);
        }
        root.put("ext", ext);
        String json = JsonUtil.object2String(root);
        LoggerUtil.info(logger.name(), json);
    }
}