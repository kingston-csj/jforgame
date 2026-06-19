package jforgame.data.annotation;


import jforgame.data.Container;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field or method as an index
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String name() default "";

    /**
     * Whether this is a unique index
     * When set to true, index values cannot be duplicated, meaning one index value can only correspond to one data record
     * {@link jforgame.data.Container#getUniqueRecordByIndex(String, Object)}
     * If duplicate index values exist in configuration data, an exception will be thrown during data loading {@link Container#inject}
     * @return true indicates a unique index
     */
    boolean unique() default false;
}
