package com.kingston.jforgame.merge.service;

import com.kingston.jforgame.merge.model.AccountEntMergeTable;
import com.kingston.jforgame.merge.model.MergeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MergedTableRegister {

    private static MergedTableRegister self ;

    /**
     * 表格--删除
     */
    public static final byte STRATEGY_CLEAR = 1;
    /**
     * 表格--直接合并
     */
    public static final byte STRATEGY_MERGE = 2;

    /**
     * 表格--交叉合并
     */
    public static final byte STRATEGY_CROSS = 3;

    private Map<String, Byte> tablesStrategys = new HashMap<>();

    private Map<String, MergeTable> tableMerge = new HashMap<>();

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
        addTableStrategy("t_account", STRATEGY_CROSS);

        tableMerge.put("t_account", new AccountEntMergeTable());
    }

    public List<String> listToDeleteTables() {
        List<String> tables = new ArrayList<>();
        for (Map.Entry<String, Byte> entry : tablesStrategys.entrySet()) {
            if (entry.getValue() == STRATEGY_CLEAR) {
                tables.add(entry.getKey());
            }
        }
//        tablesStrategys.entrySet().stream().filter(e -> e.getValue() == STRATEGY_CLEAR).collect(Collectors.toList());
        return tables;
    }

    public List<String> listToMergeDirectlyTables() {
        List<String> tables = new ArrayList<>();
        for (Map.Entry<String, Byte> entry : tablesStrategys.entrySet()) {
            if (entry.getValue() == STRATEGY_MERGE) {
                tables.add(entry.getKey());
            }
        }
        return tables;
    }

    public List<String> listToMergeCrossTables() {
        List<String> tables = new ArrayList<>();
        for (Map.Entry<String, Byte> entry : tablesStrategys.entrySet()) {
            if (entry.getValue() == STRATEGY_CROSS) {
                tables.add(entry.getKey());
            }
        }
        return tables;
    }

    public MergeTable getTableMergeBehavior(String table) {
        return tableMerge.get(table);
    }

}
