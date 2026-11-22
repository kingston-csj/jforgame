package jforgame.commons.eventbus;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an event subscriber.
 * <p>
 * In a non-Spring environment, subscriber classes can be discovered by package scanning
 * (see {@link jforgame.commons.util.ClassScanner#listClassesWithAnnotation(String, Class)}) instead of being registered manually.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventSubscriber {

}
