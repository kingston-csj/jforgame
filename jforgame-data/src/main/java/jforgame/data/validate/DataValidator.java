package jforgame.data.validate;

import jforgame.data.exception.DataValidateException;

/**
 * 数据完整性校验器
 */
public interface DataValidator {

    void check(Class<?> configClass) throws DataValidateException;
}
