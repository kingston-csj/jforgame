package jforgame.data.common;

/**
 * Default string converter, does nothing
 */
public class NullInjectParser implements ConfigValueParser {

    @Override
    public Object convert(String source) {
        return source;
    }
}
