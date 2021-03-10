package jforgame.server.tools;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jforgame.orm.utils.DbHelper;
import jforgame.orm.utils.StringUtils;
import jforgame.server.db.DbUtils;

/**
 * generate db pojo class from jdbc table automatically
 * @author kinson
 */
public class Table2DbPojo {

	private static Map<String, String> jdbcType2JavaType = new HashMap<>();

	static{
		//TODO add other types
		jdbcType2JavaType.put("TINYINT", "byte");
		jdbcType2JavaType.put("INT", "int");
		jdbcType2JavaType.put("BIGINT", "long");
		jdbcType2JavaType.put("VARCHAR", "String");
	}


	public static void main(String[] args) throws Exception {
		DbUtils.init();

		String tableName = "player";
		Connection conn = DbHelper.getConnection(DbUtils.DB_USER);
		DatabaseMetaData meta = conn.getMetaData();

		writeLine("import com.kinson.db.BaseEntity;");
		writeLine("import com.kinson.orm.annotation.Column;");
		writeLine("import com.kinson.orm.annotation.Entity;");
		writeLine("import com.kinson.orm.annotation.Id;");

		writeLine("\n\n@Entity");
		writeLine("public class " + tableName + " extends BaseEntity {\n\n");

		List<Column> columns = listTableColumns(meta, tableName);
		Set<String> pKeys = listPrimaryKeys(meta, conn.getCatalog().toLowerCase(), tableName);

		for (Column c: columns) {
			String columnName = c.name;
			String columnType = c.type;
			if (pKeys.contains(columnName)) {
				writeLine("@Id");
			}
			if (c.comment.trim().length() > 0) {
				writeLine("/**");
				writeLine(" "+ c.comment);
				writeLine("*/");
			}
			writeLine("@Column");
			writeLine("private "+ jdbcType2JavaType.get(columnType)+ " " +  columnName +";\n");
		}

		for (Column c: columns) {
			writeGetter(c);
			writeSetter(c);
		}

		writeLine("}\n\n");

	}

	private static List<Column> listTableColumns(DatabaseMetaData meta, String tableName)
		throws Exception {
		List<Column> result = new ArrayList<>();
		ResultSet colRet = meta.getColumns(null,"%", tableName,"%");
		while(colRet.next()) {
			String columnName = colRet.getString("COLUMN_NAME");
			String columnType = colRet.getString("TYPE_NAME");
			String comment = colRet.getString("REMARKS");

			result.add(Column.valueOf(columnName, columnType, comment));
		}
		return result;
	}

	private static Set<String> listPrimaryKeys(DatabaseMetaData meta, String catalog, String tableName)
			throws Exception {
		Set<String> keys = new HashSet<>();
		ResultSet pkRSet = meta.getPrimaryKeys(catalog, null, tableName);
		while(pkRSet.next() ) {
			keys.add(pkRSet.getString(4));
		}
		return keys;
	}

	private static void writeGetter(Column c) {
		String name = c.name;
		String type = c.type;
		writeLine("public " + jdbcType2JavaType.get(type) +
				  " get" + StringUtils.firstLetterToUpperCase(name) + " {");
		writeLine("\treturn " + name +";");
		writeLine("}\n");
	}

	private static void writeSetter(Column c) {
		String name = c.name;
		String type = c.type;
		writeLine("public void set" + StringUtils.firstLetterToUpperCase(name) +
				 "(" + jdbcType2JavaType.get(type) + " " + name + ") {");
		writeLine("\tthis." + name + " = " + name + ";");
		writeLine("}\n");
	}

	private static void writeLine(String content) {
		System.err.println(content);
	}


	static class Column {
		String name;
		/** jdbc column type */
		String type;

		String comment;

		static Column valueOf(String name, String type, String comment) {
			Column c = new Column();
			c.name = name;
			c.type = type;
			c.comment = comment;

			return c;
		}
	}

}
