package jforgame.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Foreign key constraint
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {

    /**
     * Constraint reference target
     *
     * @return the class of the target configuration table referenced by this field
     */
    Class<?> refer();

}
