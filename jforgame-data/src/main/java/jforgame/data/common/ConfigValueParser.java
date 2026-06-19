package jforgame.data.common;

import org.springframework.core.convert.ConversionService;

/**
 * Spring's original converter {@link ConversionService} converts based on source type and target type.
 * If the source type is just a string but there are multiple target types, this interface can be used for conversion.
 * Supports dynamic extension.
 * For example:
 * 1. Source type is String, target type is String[]
 * 2. Source type is String, target type is int[]
 * During framework initialization, {@link ConfigValueParser} is used first for conversion. If not found, {@link ConversionService} is used.
 *
 * @param <T> the custom parsed parameter type
 */
public interface ConfigValueParser<T> {


    /**
     * Field conversion
     *
     * @param source the source string
     * @return the converted object
     */
    T convert(String source);

}
