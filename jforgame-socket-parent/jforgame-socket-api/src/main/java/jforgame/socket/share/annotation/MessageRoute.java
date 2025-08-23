package jforgame.socket.share.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 消息路由注解，用于标记一个类为消息路由类
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageRoute {

    /**
     * 对应的模块号
     * {@see MessageMeta#module()}
     * 可选项，默认值为0
     */
    short module() default 0;
}
