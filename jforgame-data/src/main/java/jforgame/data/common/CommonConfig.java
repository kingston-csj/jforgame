package jforgame.data.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Common system item annotation, injects configuration items with the same name as {@link CommonData#getKey()} into properties of {@link org.springframework.stereotype.Service}
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommonConfig {

    /**
     * Configuration table field key value
     *
     * @return the key corresponding to the common configuration table
     */
    String value() default "";

    /**
     * Configuration table field parser, converts string to non-primitive type
     * @return the corresponding custom parameter converter
     */
    Class<? extends ConfigValueParser> parser() default NullInjectParser.class;
}
