package jforgame.data;

import jforgame.data.annotation.DataTable;
import jforgame.data.annotation.Id;
import jforgame.data.annotation.Index;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TableDefinition {

    /**
     * Table unique primary key
     */
    private ColumnMeta idMeta;

    /**
     * Table index columns
     */
    private final Map<String, IndexMeta> indexMetaMap = new HashMap<>();

    private Class clazz;

    /**
     * Configuration table name (default is lowercase class name)
     */
    private String resourceTable;

    public TableDefinition(Class clazz) {
        this.clazz = clazz;
        this.init();
    }

    private void init() {
        DataTable dataTable = (DataTable) clazz.getAnnotation(DataTable.class);
        if (StringUtils.isEmpty(dataTable.name())) {
            this.resourceTable = clazz.getSimpleName().toLowerCase();
        } else {
            this.resourceTable = dataTable.name();
        }

        Class curr = clazz;
        while (curr != Object.class) {
            Arrays.stream(curr.getDeclaredFields()).filter(f -> f.getAnnotation(Id.class) != null)
                    .forEach(f -> {
                        ColumnMeta indexMeta = new FieldColumnMeta(f);
                        String key = indexMeta.getName();
                        if (idMeta != null) {
                            throw new RuntimeException(String.format("%s class has multiple primary key fields-->%s", clazz.getName(), key + " and " + idMeta.getName()));
                        }
                        idMeta = indexMeta;
                    });
            curr = curr.getSuperclass();
        }


        if (idMeta == null) {
            throw new RuntimeException(String.format("%s class primary key does not exist", clazz.getName()));
        }

        curr = clazz;
        while (curr != Object.class) {
            Arrays.stream(curr.getDeclaredFields()).filter(f -> f.getAnnotation(Index.class) != null)
                    .forEach(f -> {
                        IndexMeta indexMeta = new FieldIndexMeta(f);
                        String key = indexMeta.getName();
                        if (indexMetaMap.put(key, indexMeta) != null) {
                            throw new RuntimeException(String.format("%s class index field duplicate-->%s", clazz.getName(), key + " and " + indexMetaMap.get(key).getName()));
                        }
                        indexMetaMap.put(key, indexMeta);
                    });
            curr = curr.getSuperclass();
        }


        curr = clazz;
        while (curr != Object.class) {
            Arrays.stream(curr.getDeclaredMethods()).filter(m -> m.getAnnotation(Index.class) != null)
                    .forEach(m -> {
                        Index index = m.getAnnotation(Index.class);
                        IndexMeta indexMeta = new MethodIndexMeta(index, m);
                        String key = indexMeta.getName();
                        if (indexMetaMap.put(key, indexMeta) != null) {
                            throw new RuntimeException(String.format("%s class index field duplicate-->%s", clazz.getName(), key + " and " + indexMetaMap.get(key).getName()));
                        }
                        indexMetaMap.put(key, indexMeta);
                    });
            curr = curr.getSuperclass();
        }

    }

    ColumnMeta getIdMeta() {
        return idMeta;
    }

    Map<String, IndexMeta> getIndexMetaMap() {
        return indexMetaMap;
    }

    public Class getClazz() {
        return clazz;
    }

    public String getResourceTable() {
        return resourceTable;
    }

    public void setResourceTable(String resourceTable) {
        this.resourceTable = resourceTable;
    }
}
