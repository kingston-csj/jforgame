package jforgame.data.common;

/**
 * 默认字符串转换器，不做任何处理
 */
public class NullInjectParser implements ConfigValueParser {

    @Override
    public Object convert(String source) {
        return source;
    }
}
