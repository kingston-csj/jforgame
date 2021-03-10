package jforgame.orm.ddl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TableMetadata {

    private static Logger LOG = LoggerFactory.getLogger(TableMetadata.class);

    private final String catalog;
    private final String schema;
    private final String name;
    private final Map<String, ColumnMetadata> columns = new HashMap<>();
    private final Map<String, IndexMetadata> indexes = new HashMap<>();

    TableMetadata(ResultSet rs, DatabaseMetaData meta, boolean extras) throws SQLException {
        catalog = rs.getString( "TABLE_CAT" );
        schema = rs.getString( "TABLE_SCHEM" );
        name = rs.getString( "TABLE_NAME" );
        initColumns( meta );
        if ( extras ) {
            initIndexes( meta );
        }
    }

    public String getName() {
        return name;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getSchema() {
        return schema;
    }

    @Override
    public String toString() {
        return "TableMetadata(" + name + ')';
    }

    public ColumnMetadata getColumnMetadata(String columnName) {
        return columns.get( columnName.toLowerCase( Locale.ROOT ) );
    }

    public IndexMetadata getIndexMetadata(String indexName) {
        return indexes.get( indexName.toLowerCase( Locale.ROOT ) );
    }

    private void addIndex(ResultSet rs) throws SQLException {
        String index = rs.getString( "INDEX_NAME" );

        if ( index == null ) {
            return;
        }

        IndexMetadata info = getIndexMetadata( index );
        if ( info == null ) {
            info = new IndexMetadata( rs );
            indexes.put( info.getName().toLowerCase( Locale.ROOT ), info );
        }

        info.addColumn( getColumnMetadata( rs.getString( "COLUMN_NAME" ) ) );
    }

    public void addColumn(ResultSet rs) throws SQLException {
        String column = rs.getString( "COLUMN_NAME" );

        if ( column == null ) {
            return;
        }

        if ( getColumnMetadata( column ) == null ) {
            ColumnMetadata info = new ColumnMetadata( rs );
            columns.put( info.getName().toLowerCase( Locale.ROOT ), info );
        }
    }

    private void initIndexes(DatabaseMetaData meta) throws SQLException {
        ResultSet rs = null;

        try {
            rs = meta.getIndexInfo( catalog, schema, name, false, true );

            while ( rs.next() ) {
                if ( rs.getShort( "TYPE" ) == DatabaseMetaData.tableIndexStatistic ) {
                    continue;
                }
                addIndex( rs );
            }
        }
        finally {
            if ( rs != null ) {
                rs.close();
            }
        }
    }

    private void initColumns(DatabaseMetaData meta) throws SQLException {
        ResultSet rs = null;

        try {
            rs = meta.getColumns( catalog, schema, name, "%" );
            while ( rs.next() ) {
                addColumn( rs );
            }
        }
        finally {
            if ( rs != null ) {
                rs.close();
            }
        }
    }
}