package jforgame.orm.ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
     * Database table field schema
     */
class ColumnMetadata {

    private final String name;
    private final String typeName;
    /**
     * Full type name including length information
     */
    private final String fullTypeName;
    private final int columnSize;
    private final int decimalDigits;
    private final String isNullable;
    private final int typeCode;

    ColumnMetadata(ResultSet rs) throws SQLException {
        name = rs.getString("COLUMN_NAME");
        columnSize = rs.getInt("COLUMN_SIZE");
        decimalDigits = rs.getInt("DECIMAL_DIGITS");
        isNullable = rs.getString("IS_NULLABLE");
        typeCode = rs.getInt("DATA_TYPE");
        typeName = new StringTokenizer(rs.getString("TYPE_NAME"), "() ").nextToken();
        
        // Build full type name including length information
        String baseTypeName = rs.getString("TYPE_NAME");
        if (columnSize > 0) {
            // For integer types and long text types, ignore length information
            if (isIntegerType(typeCode) || isLongTextType(baseTypeName)) {
                fullTypeName = baseTypeName;
            } else if (decimalDigits > 0) {
                fullTypeName = baseTypeName + "(" + columnSize + "," + decimalDigits + ")";
            } else {
                fullTypeName = baseTypeName + "(" + columnSize + ")";
            }
        } else {
            fullTypeName = baseTypeName;
        }
    }

    public String getName() {
        return name;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getFullTypeName() {
        return fullTypeName;
    }

    public int getColumnSize() {
        return columnSize;
    }

    public int getDecimalDigits() {
        return decimalDigits;
    }

    public String getNullable() {
        return isNullable;
    }

    public String toString() {
        return "ColumnMetadata(" + name + ')';
    }

    public int getTypeCode() {
        return typeCode;
    }

    /**
     * Check if it is an integer type
     * @param typeCode SQL type code
     * @return whether it is an integer type
     */
    private boolean isIntegerType(int typeCode) {
        // Determine integer type based on java.sql.Types definition
        return typeCode == java.sql.Types.TINYINT ||
               typeCode == java.sql.Types.SMALLINT ||
               typeCode == java.sql.Types.INTEGER ||
               typeCode == java.sql.Types.BIGINT;
    }

    /**
     * Check if it is a long text type
     * @param typeName type name
     * @return whether it is a long text type
     */
    private boolean isLongTextType(String typeName) {
        String lowerTypeName = typeName.toLowerCase();
        return lowerTypeName.equals("longtext") ||
               lowerTypeName.equals("mediumtext") ||
               lowerTypeName.equals("text") ||
               lowerTypeName.equals("longblob") ||
               lowerTypeName.equals("mediumblob") ||
               lowerTypeName.equals("blob");
    }
}