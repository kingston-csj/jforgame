package jforgame.merge.service;

import jforgame.merge.model.AccountEntMergeTable;
import jforgame.merge.model.MergeTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergedTableRegistry {

    private static MergedTableRegistry self ;

    /**
     * 表格--删除从服
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

    /**
     * 母服合服前需要执行的sql
     */
    private List<String> parentBeforeSqls = new ArrayList<>();

    /**
     * 母服合服后需要执行的sql
     */
    private List<String> parentAfterSqls = new ArrayList<>();

    public static MergedTableRegistry getInstance() {
        if (self != null) {
            return self;
        }
        synchronized (MergedTableRegistry.class) {
            if (self == null) {
                self = new MergedTableRegistry();
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

    public List<String> getParentBeforeSqls() {
        return parentBeforeSqls;
    }

    public List<String> getParentAfterSqls() {
        return parentAfterSqls;
    }

}
