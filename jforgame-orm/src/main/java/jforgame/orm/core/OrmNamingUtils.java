package jforgame.orm.core;

import jforgame.commons.util.StringUtil;

import javax.persistence.Entity;

/**
 * ORM 命名规则工具，统一运行期与 DDL 生成期的默认命名。
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
