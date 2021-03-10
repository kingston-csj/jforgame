package jforgame.server.game.database.config.storage;

import jforgame.server.db.DbUtils;
import jforgame.server.game.database.config.Reloadable;
import jforgame.server.game.database.config.bean.ConfigCross;
import jforgame.server.logs.LoggerUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ConfigCrossStorage implements Reloadable {

    private Map<Integer, ConfigCross> configs;

    @Override
    public void reload() {
        String sql = "SELECT * FROM ConfigCross";
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
