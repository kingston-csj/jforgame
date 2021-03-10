package jforgame.orm;

import jforgame.common.utils.ClassScanner;
import jforgame.orm.exception.OrmConfigException;
import jforgame.orm.utils.StringUtils;

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
     * entity与对应的ormbridge的映射关系
     */
    private Map<Class<?>, OrmBridge> classOrmMapper = new HashMap<>();

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
        if (entity.name().length() <= 0) {
            bridge.setTableName(StringUtils.firstLetterToLowerCase(clazz.getSimpleName()));
        } else {
            bridge.setTableName(entity.name());
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Column column = field.getAnnotation(Column.class);
            String fieldName = field.getName();
            try {
                if (column == null) {
                    continue;
                }
                FieldMetadata metadata = FieldMetadata.valueOf(field);
                bridge.addFieldMetadata(fieldName, metadata);
                if (field.getAnnotation(Id.class) != null) {
                    bridge.addUniqueKey(fieldName);
                }
                if (!StringUtils.isEmpty(column.name())) {
                    bridge.addPropertyColumnOverride(fieldName, column.name());
                }
            } catch (Exception e) {
                throw new OrmConfigException(e);
            }
            //如果实体没有主键的话，一旦涉及更新，会影响整张表数据，后果是灾难性的
            if (bridge.getQueryProperties().size() <= 0) {
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
