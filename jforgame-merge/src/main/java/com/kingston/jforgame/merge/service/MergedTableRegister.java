package com.kingston.jforgame.merge.service;

import java.util.HashMap;
import java.util.Map;

public class MergedTableRegister {

    private static MergedTableRegister self ;

    /**
     * 表格--删除
     */
    public static final byte STRATEGY_CLEAR = 1;
    /**
     * 表格--直接合并
     */
    public static final byte STRATEGY_MERGE = 1;

    /**
     * 表格--交叉合并
     */
    public static final byte STRATEGY_CROSS = 1;

    private Map<String, Byte> tablesStrategys = new HashMap<>();

    public static MergedTableRegister getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (MergedTableRegister.class) {
            if (self == null) {
                self = new MergedTableRegister();
                self.init();
            }
        }
        return self;
    }

    private void addTableStrategy(String tableName, byte strategy) {
        tablesStrategys.put(tableName, strategy);
    }

    private void init() {
        addTableStrategy("t_role", STRATEGY_MERGE);
        addTableStrategy("t_rank", STRATEGY_CLEAR);
    }

}
