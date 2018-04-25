package com.kingston.jforgame.common.utils;

/**
 * 类扫描过滤器
 * @author kingston
 */
@FunctionalInterface
public interface ClassFilter {

    /**
     * 是否满足条件
     * @param clazz
     * @return
     */
    boolean accept(Class<?> clazz);

}