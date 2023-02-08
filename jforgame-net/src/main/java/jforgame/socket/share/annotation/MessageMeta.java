package jforgame.socket.share.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation type is used in ordinal java bean
 * to specify module and cmd of the given message.
 * @author kinson
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMeta {

	/**
	 * indicate the message pipeline direction,
	 * some from client to server, or from server to server
	 */
	byte source() default 0;

	/**
	 * module of the message
	 * @return
	 */
	short module() default 0;

	/**
	 * cmd of the message
	 * @return
	 */
	int cmd() default 0;

}
