package jforgame.data.common;

import org.springframework.core.convert.ConversionService;

/**
 * spring原始转化器{@link ConversionService}是根据源类型和目标类型进行转化的，
 * 如果源类型只是字段串，但目标类型有多种，那么可以使用该接口进行转化。
 * 支持动态拓展
 * 例如：
 * 1. 源类型为String，目标类型为List<String>
 * 2. 源类型为String，目标类型为int[]
 * 框架在初始化的时候，会优先 使用{@link ConfigValueParser}进行转化，如果没有找到，再使用{@link ConversionService}进行转化。
 *
 * @param <T>
 */
public interface ConfigValueParser<T> {


    /**
     * 字段转化
     *
     * @param source 源字符串
     * @return 转化后的对象
     */
    T convert(String source);

}
