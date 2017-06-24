package com.kingston.net.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Protocol {
	
	/** 消息所属模块号 */
	
	short module();
	/** 消息所属子类型 */
	short cmd();

}
