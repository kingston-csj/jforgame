package jforgame.orm.core;

import jforgame.commons.ClassScanner;
import jforgame.commons.StringUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public enum OrmProcessor {

    /**
     * 枚举单例
     */
    INSTANCE;

    /**
     * entity与对应bridge的映射关系
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
        Entity entity = clazz.getAnnotation(Entity.class);
        //没有设置tablename,则用entity名首字母小写
        if (entity.name().isEmpty()) {
            bridge.setTableName(StringUtil.firstLetterToLowerCase(clazz.getSimpleName()));
        } else {
            bridge.setTableName(entity.name());
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (field.getAnnotation(Id.class) != null) {
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
            //如果实体没有主键的话，一旦涉及更新，会影响整张表数据，后果是灾难性的
            if (bridge.getPrimaryKeyProperties().isEmpty()) {
                throw new OrmConfigException(clazz.getSimpleName() + " entity 没有查询索引主键字段");
            }
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
