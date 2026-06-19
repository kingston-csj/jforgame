package jforgame.orm.core;

import jforgame.commons.util.ClassScanner;
import jforgame.commons.util.StringUtil;
import jforgame.orm.entity.StatefulEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum OrmProcessor {

    /**
     * Enum singleton
     */
    INSTANCE;

    /**
     * Mapping between entity and its corresponding bridge
     */
    private final Map<Class<?>, OrmBridge> classOrmMapper = new HashMap<>();

    /**
     * @param scanPath path to load orm entities
     */
    public void initOrmBridges(String scanPath) {
        Set<Class<?>> entityClazzs = listEntityClazzs(scanPath);

        for (Class<?> clazz : entityClazzs) {
            OrmBridge bridge = createBridge(clazz);
            this.classOrmMapper.put(clazz, bridge);
        }
    }

    private OrmBridge createBridge(Class<?> clazz) {
        OrmBridge bridge = new OrmBridge();
        bridge.setTableName(OrmNamingUtils.resolveTableName(clazz));

        // Start from current class, traverse all parent classes
        Class<?> currClazz = clazz;
        while (currClazz != StatefulEntity.class && currClazz != Object.class) {
            Field[] fields = currClazz.getDeclaredFields();
            for (Field field : fields) {
                String fieldName = field.getName();
                if (field.getAnnotation(Id.class) != null) {
//                    // Cannot be primitive type
//                    if (field.getType().isPrimitive()) {
//                        throw new OrmConfigException(clazz.getSimpleName() + " entity primary key field cannot be primitive type");
//                    }
                    bridge.addUniqueKey(fieldName);
                }
                Column column = field.getAnnotation(Column.class);
                try {
                    if (column == null) {
                        continue;
                    }
                    FieldMetaData metadata = FieldMetaData.valueOf(field);
                    bridge.addFieldMetadata(fieldName, metadata);
                    if (!StringUtil.isEmpty(column.name())) {
                        bridge.addPropertyColumnOverride(fieldName, column.name());
                    }
                } catch (Exception e) {
                    throw new OrmConfigException(e);
                }
            }
            currClazz = currClazz.getSuperclass();
        }

        // If the entity has no primary key, once update is involved, it will affect the entire table data, which is catastrophic
        if (bridge.getPrimaryKeyProperties().isEmpty()) {
            throw new OrmConfigException(clazz.getSimpleName() + " entity has no indexed primary key field");
        }

        return bridge;
    }

    private Set<Class<?>> listEntityClazzs(String scanPath) {
        return ClassScanner.listClassesWithAnnotation(scanPath, Entity.class);
    }

    public OrmBridge getOrmBridge(Class<?> clazz) {
        return this.classOrmMapper.get(clazz);
    }
}
