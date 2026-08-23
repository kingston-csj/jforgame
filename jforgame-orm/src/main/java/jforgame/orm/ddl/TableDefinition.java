package jforgame.orm.ddl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class TableDefinition {

    private String tableName;

    private Map<String, ColumnDefinition> columns = new LinkedHashMap<>();

    private List<IndexMetadata> indexes = new ArrayList<>();

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

    public void addIndex(IndexMetadata index) {
        indexes.add(index);
    }

    public Map<String, ColumnDefinition> getColumns() {
        return columns;
    }

    public List<IndexMetadata> getIndexes() {
        return indexes;
    }

    public String sqlCreateString() {
        StringBuilder buf = new StringBuilder("create table ")
                .append(' ')
                .append(tableName)
                .append(" (");
        Iterator<ColumnDefinition> it = columns.values().stream().iterator();
        while (it.hasNext()) {
            ColumnDefinition col = it.next();
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

            String columnComment = col.getComment();
            if (columnComment != null) {
                buf.append(columnComment);
            }
            if (it.hasNext()) {
                buf.append(", ");
            }
        }

        buf.append(')');
        if (comment != null) {
            buf.append(comment);
        }

        return buf.toString();
    }

    public List<String> sqlCreateIndexStrings() {
        List<String> result = new ArrayList<>();
        for (IndexMetadata index : indexes) {
            result.add(index.toSql(tableName));
        }
        return result;
    }

    public Iterator<String> sqlAlterStrings(TableMetadata tableMetadata) {
        StringBuilder root = new StringBuilder("ALTER TABLE " + tableName)
                .append(' ');

        Iterator<ColumnDefinition> iter = columns.values().iterator();
        List<String> results = new ArrayList<>();

        while (iter.hasNext()) {
            final ColumnDefinition column = iter.next();
            ColumnMetadata columnInfo = tableMetadata.getColumnMetadata(column.getName());
            if (columnInfo == null) {
                StringBuilder alter = new StringBuilder(root.toString())
                        .append(" ADD COLUMN ")
                        .append(column.getName())
                        .append(' ')
                        .append(column.getJdbcType());

                String defaultValue = column.getDefaultValue();
                if (defaultValue != null) {
                    alter.append(" DEFAULT ").append(defaultValue);
                }

                if (column.isNullable()) {
                    alter.append(" ");
                } else {
                    alter.append(" NOT NULL");
                }
                if (column.isPrimary()) {
                    alter.append("  PRIMARY KEY ");
                }

                String columnComment = column.getComment();
                if (columnComment != null) {
                    alter.append(columnComment);
                }
                results.add(alter.toString());
            }
        }

        Set<String> existingIndexKeys = new HashSet<>();
        for (IndexMetadata dbIdx : tableMetadata.getIndexes().values()) {
            existingIndexKeys.add(dbIdx.signatureKey());
        }
        for (IndexMetadata codeIdx : indexes) {
            if (!existingIndexKeys.contains(codeIdx.signatureKey())) {
                boolean sameNameDiff = false;
                for (IndexMetadata dbIdx : tableMetadata.getIndexes().values()) {
                    if (dbIdx.getName().equalsIgnoreCase(codeIdx.getName())) {
                        sameNameDiff = true;
                        break;
                    }
                }
                if (!sameNameDiff) {
                    results.add(codeIdx.toSql(tableName));
                }
            }
        }

        return results.iterator();
    }

}
