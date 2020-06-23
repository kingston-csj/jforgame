package com.kingston.jforgame.server.game.database.config.storage;

import com.kingston.jforgame.server.db.DbUtils;
import com.kingston.jforgame.server.game.database.config.CommonConfigs;
import com.kingston.jforgame.server.game.database.config.Reloadable;
import com.kingston.jforgame.server.game.database.config.bean.ConfigConstant;
import com.kingston.jforgame.server.game.database.config.bean.ConfigCross;
import com.kingston.jforgame.server.logs.LoggerUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigCrossStorage implements Reloadable {

    private Map<Integer, ConfigCross> configs;

    @Override
    public void reload() {
        String sql = "SELECT * FROM ConfigConstant";
        try {
            List<ConfigCross> datas = DbUtils.queryMany(DbUtils.DB_DATA, sql, ConfigCross.class);
            configs = datas.stream().collect(Collectors.toMap(ConfigCross::getId, Function.identity()));
        } catch (Exception e) {
            LoggerUtils.error("", e);
        }
    }

    public ConfigCross getConfigCrossBy(int serverId) {
        return configs.get(serverId);
    }
}
