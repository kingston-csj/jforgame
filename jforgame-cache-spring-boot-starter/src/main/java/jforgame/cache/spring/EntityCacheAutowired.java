package jforgame.cache.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for automatic injection of entity cache services.
 * Fields annotated with this will be automatically injected with the corresponding
 * {@link jforgame.cache.BaseEntityCacheService} instance.
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EntityCacheAutowired {
}
