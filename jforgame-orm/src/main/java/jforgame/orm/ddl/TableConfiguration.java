package jforgame.orm.ddl;

import jforgame.commons.util.StringUtil;
import jforgame.orm.core.OrmNamingUtils;
import jforgame.orm.entity.StatefulEntity;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TableConfiguration {

    private Map<String, TableDefinition> tables = new HashMap<>();

    public void register(Set<Class<?>> codeTables) {
        for (Class<?> entity : codeTables) {
            String tableName = OrmNamingUtils.resolveTableName(entity);
            TableDefinition tableDefinition = new TableDefinition();
            tableDefinition.setTableName(tableName);
            tables.put(tableName, tableDefinition);
            fillColumns(entity, tableDefinition);
            fillIndexes(entity, tableDefinition);
        }
    }

    private void fillColumns(Class<?> entity, TableDefinition tableDefinition) {
        Class<?> currClazz = entity;
        // Traverse parent classes, add parent class fields to table definition as well
        while (currClazz != StatefulEntity.class) {
            Arrays.stream(currClazz.getDeclaredFields()).filter(e -> e.getAnnotation(Column.class) != null)
                    .forEach(f -> {
                        Column column = f.getAnnotation(Column.class);
                        ColumnDefinition columnDef = new ColumnDefinition();
                        if (StringUtil.isNotEmpty(column.name())) {
                            columnDef.setName(column.name());
                        } else {
                            columnDef.setName(f.getName());
                        }
                        columnDef.setPrimary(f.getAnnotation(Id.class) != null);
                        if (f.isAnnotationPresent(Id.class)) {
                            columnDef.setNullable(false);
                        } else {
                            columnDef.setNullable(column.nullable());
                        }
                        columnDef.setJdbcType(f.getType(), column.columnDefinition());
                        tableDefinition.addColumn(columnDef);
                    });
            currClazz = currClazz.getSuperclass();
        }
    }

    private void fillIndexes(Class<?> entity, TableDefinition tableDefinition) {
        Table tableAnn = entity.getAnnotation(Table.class);
        if (tableAnn == null) {
            return;
        }
        Index[] indexes = tableAnn.indexes();
        if (indexes == null) {
            return;
        }
        for (Index idx : indexes) {
            String name = idx.name();
            if (StringUtil.isEmpty(name)) {
                continue;
            }
            String columnList = idx.columnList();
            if (StringUtil.isEmpty(columnList)) {
                continue;
            }
            String[] parts = columnList.split(",");
            List<String> columnNames = new ArrayList<>(parts.length);
            for (String p : parts) {
                String trimmed = p.trim();
                if (StringUtil.isNotEmpty(trimmed)) {
                    columnNames.add(trimmed);
                }
            }
            if (columnNames.isEmpty()) {
                continue;
            }
            IndexMetadata meta = new IndexMetadata(name, idx.unique(), columnNames);
            tableDefinition.addIndex(meta);
        }
    }

    public Map<String, TableDefinition> getTables() {
        return tables;
    }

}
