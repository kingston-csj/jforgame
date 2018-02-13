package com.kingston.jforgame.common.utils;

/**
 * 类扫描过滤器
 */
public interface ClassFilter {

    /**
     * 是否满足条件
     * @param clazz
     * @return
     */
    boolean accept(Class<?> clazz);

}