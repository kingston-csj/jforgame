package jforgame.orm.core;

import jforgame.commons.util.StringUtil;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * ORM naming utility, unifies default naming for runtime and DDL generation.
 */
public final class OrmNamingUtils {

    private OrmNamingUtils() {
    }

    public static String resolveTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        if (table != null && StringUtil.isNotEmpty(table.name())) {
            return table.name();
        }
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (entity != null && StringUtil.isNotEmpty(entity.name())) {
            return entity.name();
        }
        return StringUtil.firstLetterToLowerCase(entityClass.getSimpleName());
    }
}
