package jforgame.socket.core.protocol.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Message route annotation, used to mark a class as a message route class
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageRoute {

    /**
     * Corresponding module number (optional).
     * Prefer to use this parameter instead of {@link MessageMeta#module()}, to avoid duplicate declaration of protocols in the same module.
     * @return module number
     */
    short module() default 0;
}
