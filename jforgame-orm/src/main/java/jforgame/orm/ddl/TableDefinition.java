package jforgame.orm.ddl;

import jforgame.commons.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class TableDefinition {

    private String tableName;

    private Map<String, ColumnDefinition> columns = new LinkedHashMap<>();

//    private Map<String, Index> indexes = new LinkedHashMap<>();

    private String comment;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void addColumn(ColumnDefinition column) {
        columns.put(column.getName(), column);
    }

    public String sqlCreateString() {
        StringBuilder buf = new StringBuilder("create table ")
                .append(' ')
                .append(tableName)
                .append(" (");
        List<String> list = columns.values().stream().map(ColumnDefinition::getName).collect(Collectors.toList());
        List<String> orderColumns = orderColumns(list, true);
        Iterator<String> it = orderColumns.iterator();
        while (it.hasNext()) {
            String orderColumn = it.next();
            ColumnDefinition col = columns.get(orderColumn);
            buf.append(col.getName())
                    .append(' ');
            buf.append(col.getJdbcType());
            String defaultValue = col.getDefaultValue();
            if (defaultValue != null) {
                buf.append(" default ").append(defaultValue);
            }
            if (col.isNullable()) {
                buf.append(" ");
            } else {
                buf.append(" not null");
            }
            if (col.isPrimary()) {
                buf.append("  PRIMARY KEY ");
            }
//            if ( col.isUnique() ) {
//                String keyName = Constraint.generateName( "UK_", this, col );
//                UniqueKey uk = getOrCreateUniqueKey( keyName );
//                uk.addColumn( col );
//                buf.append( dialect.getUniqueDelegate()
//                        .getColumnDefinitionUniquenessFragment( col ) );
//            }

            String columnComment = col.getComment();
            if (columnComment != null) {
                buf.append(columnComment);
            }
            if (it.hasNext()) {
                buf.append(", ");
            }
        }
//        if ( hasPrimaryKey() ) {
//            buf.append( ", " )
//                    .append( getPrimaryKey().sqlConstraintString( dialect ) );
//        }
//
        buf.append(')');
        if (comment != null) {
            buf.append(comment);
        }

        return buf.toString();
    }

    public Pair<Iterator<String>, String> sqlAlterStrings(TableMetadata tableMetadata) {
        StringBuilder root = new StringBuilder("alter table " + tableName)
                .append(' ');

        List<String> list = columns.values().stream().map(ColumnDefinition::getName).collect(Collectors.toList());
        List<String> orderColumns = orderColumns(list, false);
        Iterator<String> iter = orderColumns.iterator();
        List<String> results = new ArrayList<>();
        while (iter.hasNext()) {
            ColumnDefinition column = columns.get(iter.next());
            ColumnMetadata columnInfo = tableMetadata.getColumnMetadata(column.getName());
            if (columnInfo == null) {

                // the column doesnt exist at all.
                StringBuilder alter = new StringBuilder(root.toString())
                        .append(" add column ")
                        .append(column.getName())
                        .append(' ')
                        .append(column.getJdbcType());

                String defaultValue = column.getDefaultValue();
                if (defaultValue != null) {
                    alter.append(" default ").append(defaultValue);
                }

                if (column.isNullable()) {
                    alter.append(" ");
                } else {
                    alter.append(" not null");
                }
                if (column.isPrimary()) {
                    alter.append("  PRIMARY KEY ");
                }

                String columnComment = column.getComment();
                if (columnComment != null) {
                    alter.append(column.getComment());
                }
                results.add(alter.toString());
            }
        }
        return new Pair<>(results.iterator(), orderColumns.get(orderColumns.size() - 1));
    }

    /**
     * 对字段进行排序
     *
     * @param columns 字段列表
     * @return 排序后的字段列表
     */
    private List<String> orderColumns(List<String> columns, boolean isCreate) {
        List<String> orderedColumns = new ArrayList<>();
        Set<String> specialColumns = new HashSet<>(Arrays.asList("id", "cid", "templateId", "createTime", "updateTime"));

        // 首先添加 id
        if (columns.contains("id")) {
            orderedColumns.add("id");
        }

        // 添加 cid（如果存在）
        if (columns.contains("cid")) {
            orderedColumns.add("cid");
        }

        // 添加其他字段（除了特殊字段）
        for (String column : columns) {
            if (!specialColumns.contains(column)) {
                orderedColumns.add(column);
            }
        }
        if (isCreate) {
            // 添加 createTime 和 updateTime
            if (columns.contains("createTime")) {
                orderedColumns.add("createTime");
            }
            if (columns.contains("updateTime")) {
                orderedColumns.add("updateTime");
            }
        }

        return orderedColumns;
    }
}
