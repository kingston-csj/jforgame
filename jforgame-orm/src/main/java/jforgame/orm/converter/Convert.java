package jforgame.orm.converter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, TYPE}) @Retention(RUNTIME)
public @interface Convert {

    /**
     * Specifies the converter to be applied.  A value for this
     * element must be specified if multiple converters would
     * otherwise apply.
     */
    Class converter() default void.class;

    /**
     * The <code>attributeName</code> element must be specified unless the
     * <code>Convert</code> annotation is on an attribute of basic type
     * or on an element collection of basic type.  In these cases, the
     * <code>attributeName</code> element  must not be specified.
     */
    String attributeName() default "";

}