package jforgame.socket.share.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 在一个普通的消息类上添加此注解，以绑定消息的类型
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MessageMeta {

	/**
	 * 标记该消息的来源，例如客户端，或者服务器内部节点(可选参数)
	 * 由业务层自行定义
	 * @return 消息来源
	 */
	byte source() default 0;

	/**
	 * 消息模块号(可选参数)
	 * 由业务层自行定义
	 * @return 消息模块号
	 */
	short module() default 0;

	/**
	 * 消息类型
	 * 由业务层自行定义
	 * @return 消息类型
	 */
	int cmd() default 0;

}
