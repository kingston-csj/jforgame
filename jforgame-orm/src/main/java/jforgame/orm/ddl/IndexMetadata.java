package jforgame.orm.ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

class IndexMetadata {
    private final String name;
    private final List<String> columnNames = new ArrayList<>();
    private final boolean unique;

    IndexMetadata(ResultSet rs) throws SQLException {
        this.name = rs.getString("INDEX_NAME");
        boolean nonUnique = rs.getBoolean("NON_UNIQUE");
        this.unique = !nonUnique;
    }

    IndexMetadata(String name, boolean unique, List<String> columnNames) {
        this.name = name;
        this.unique = unique;
        if (columnNames != null) {
            this.columnNames.addAll(columnNames);
        }
    }

    public String getName() {
        return name;
    }

    public boolean isUnique() {
        return unique;
    }

    public List<String> getColumnNames() {
        return Collections.unmodifiableList(columnNames);
    }

    void addColumn(ColumnMetadata column) {
        if (column != null) {
            columnNames.add(column.getName());
        }
    }

    void addColumnName(String columnName) {
        if (columnName != null) {
            columnNames.add(columnName);
        }
    }

    public ColumnMetadata[] getColumns() {
        return new ColumnMetadata[0];
    }

    public String toSql(String tableName) {
        StringBuilder buf = new StringBuilder();
        buf.append("CREATE ");
        if (unique) {
            buf.append("UNIQUE ");
        }
        buf.append("INDEX ").append(name)
                .append(" ON ").append(tableName)
                .append(" (");
        buf.append(String.join(", ", columnNames));
        buf.append(')');
        return buf.toString();
    }

    public String signatureKey() {
        return name + "|" + unique + "|" + String.join(",", columnNames);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndexMetadata)) return false;
        IndexMetadata that = (IndexMetadata) o;
        return unique == that.unique
                && Objects.equals(name, that.name)
                && Objects.equals(columnNames, that.columnNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, columnNames, unique);
    }

    public String toString() {
        return "IndexMetadata(" + name + ", unique=" + unique + ", columns=" + columnNames + ')';
    }
}
