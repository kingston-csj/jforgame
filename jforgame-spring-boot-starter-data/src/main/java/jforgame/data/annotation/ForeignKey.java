package jforgame.data.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 外键约束
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignKey {

    /**
     * 约束参考主体
     *
     * @return 该字段参考的目标配置表对应的class
     */
    Class<?> refer();

}
