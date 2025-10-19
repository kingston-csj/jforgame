package jforgame.data.annotation;


import jforgame.data.Container;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记一个字段或者方法为索引
 */
@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String name() default "";

    /**
     * 是否唯一索引
     * 当设置为true时，索引值不能重复，表示同一个索引值只能对应一个数据
     * {@link jforgame.data.Container#getUniqueRecordByIndex(String, Object)}
     * 若配置数据中存在重复的索引值，则在加载数据时，抛出异常 {@link Container#inject}
     * @return true表示唯一索引
     */
    boolean unique() default false;
}
