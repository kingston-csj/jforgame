package jforgame.data.common;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通用系统项，将与{@link CommonData#getKey()}相同名称的配置项注入到{@link org.springframework.stereotype.Service}的属性
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommonConfig {

    /**
     * 配置表字段key值
     *
     * @return
     */
    String value() default "";

    /**
     * 配置表字段解析器，将字符串转化为非基本类型
     */
    Class<? extends ConfigValueParser> parser() default NullInjectParser.class;
}
