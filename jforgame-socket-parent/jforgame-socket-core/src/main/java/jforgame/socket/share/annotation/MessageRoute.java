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
     * 对应的模块号(可选项)
     * 优先使用该参数，而不是{@link  MessageMeta#module()}，避免同一模块下的协议都重复申明
     * @return 模块号
     */
    short module() default 0;
}
