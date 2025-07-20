package jforgame.orm.ddl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

/**
 * 数据库表字段schema
 */
class ColumnMetadata {

    private final String name;
    private final String typeName;
    /**
     * 完整的类型名称，包含长度信息
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
        
        // 构建完整的类型名称，包含长度信息
        String baseTypeName = rs.getString("TYPE_NAME");
        if (columnSize > 0) {
            // 对于整数类型和长文本类型，忽略长度信息
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
     * 判断是否为整数类型
     * @param typeCode SQL类型代码
     * @return 是否为整数类型
     */
    private boolean isIntegerType(int typeCode) {
        // 根据 java.sql.Types 定义判断整数类型
        return typeCode == java.sql.Types.TINYINT ||
               typeCode == java.sql.Types.SMALLINT ||
               typeCode == java.sql.Types.INTEGER ||
               typeCode == java.sql.Types.BIGINT;
    }

    /**
     * 判断是否为长文本类型
     * @param typeName 类型名称
     * @return 是否为长文本类型
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