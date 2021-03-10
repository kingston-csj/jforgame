package jforgame.socket.annotation;

import jforgame.socket.message.Message;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation type is used in {@link Message}
 * to specify module and cmd of the given message.
 * @author kinson
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMeta {

	short module() default 0;

	byte cmd() default 0;

}
