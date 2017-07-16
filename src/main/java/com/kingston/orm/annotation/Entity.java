package com.kingston.orm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** 
 * 
 *  标识该对象需要持久化
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
	
	/** 不为空则说明表名字跟entity类名字不一致 */
	String table() default "";
	
	/** 表实体是否可读模式 */
	boolean readOnly() default false;
}
