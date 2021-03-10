package jforgame.server.listener.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jforgame.server.listener.EventType;

/**
 * 事件处理者
 * @author kinson
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface EventHandler {
	
	/** 绑定的事件类型列表 */
	EventType[] value();
	
}
