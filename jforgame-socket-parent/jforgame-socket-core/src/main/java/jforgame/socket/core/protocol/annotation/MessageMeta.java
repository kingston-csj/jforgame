package jforgame.socket.core.protocol.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Add this annotation to a normal message class to bind the message type
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMeta {

	/**
	 * Mark the source of this message, e.g., client, or server internal node (optional parameter).
	 * Defined by business layer.
	 * @return message source
	 */
	byte source() default 0;

	/**
	 * Message module number (optional parameter).
	 * Defined by business layer.
	 * @return message module number
	 */
	short module() default 0;

	/**
	 * Message type.
	 * Defined by business layer.
	 * @return message type
	 */
	int cmd() default 0;

}
