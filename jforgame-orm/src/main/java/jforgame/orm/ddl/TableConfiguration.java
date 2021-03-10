package jforgame.orm.ddl;

import jforgame.orm.utils.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TableConfiguration {

    private Map<String, TableDefinition> tables = new HashMap<>();

    public void register(Set<Class<?>> codeTables) {
        for (Class<?> entity : codeTables) {
            String tableName = entity.getSimpleName();
            Entity annotation = entity.getAnnotation(Entity.class);
            if (StringUtils.isNotEmpty(annotation.name())) {
                tableName = annotation.name();
            }
            TableDefinition tableDefinition = new TableDefinition();
            tableDefinition.setTableName(tableName);
            tables.put(tableName, tableDefinition);
            fillColumns(entity, tableDefinition);
        }
    }

    private void fillColumns(Class<?> entity, TableDefinition tableDefinition) {
        Arrays.stream(entity.getDeclaredFields()).filter(e -> e.getAnnotation(Column.class) != null)
                .forEach(f -> {
                    Column column = f.getAnnotation(Column.class);
                    ColumnDefinition columnDef = new ColumnDefinition();
                    if (StringUtils.isNotEmpty(column.name())) {
                        columnDef.setName(column.name());
                    } else {
                        columnDef.setName(f.getName());
                    }
                    columnDef.setPrimary(f.getAnnotation(Id.class) != null);
                    columnDef.setNullable(column.nullable());
                    columnDef.setJdbcType(f.getType(), column.columnDefinition());
                    tableDefinition.addColumn(columnDef);
                });
    }

    public Map<String, TableDefinition> getTables() {
        return tables;
    }

}
