package jforgame.orm.core;

import jforgame.commons.util.StringUtil;

import javax.persistence.Entity;

/**
 * ORM naming utility, unifies default naming for runtime and DDL generation.
 */
public final class OrmNamingUtils {

    private OrmNamingUtils() {
    }

    public static String resolveTableName(Class<?> entityClass) {
        Entity entity = entityClass.getAnnotation(Entity.class);
        if (entity != null && StringUtil.isNotEmpty(entity.name())) {
            return entity.name();
        }
        return StringUtil.firstLetterToLowerCase(entityClass.getSimpleName());
    }
}
