package jforgame.orm.ddl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    public Iterator<String> sqlAlterStrings(TableMetadata tableMetadata) {
        StringBuilder root = new StringBuilder("alter table "+tableName)
                .append(' ');

        Iterator<ColumnDefinition> iter = columns.values().iterator();
        List<String> results = new ArrayList<>();

        while (iter.hasNext()) {
            final ColumnDefinition column = iter.next();
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

        return results.iterator();
    }

}
