package jforgame.data.validate;

import jforgame.data.exception.DataValidateException;

/**
 * Data integrity validator
 */
public interface DataValidator {

    void check(Class<?> configClass) throws DataValidateException;
}
