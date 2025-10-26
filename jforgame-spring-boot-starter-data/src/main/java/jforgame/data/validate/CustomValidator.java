package jforgame.data.validate;

import jforgame.data.Container;
import jforgame.data.DataRepository;
import jforgame.data.exception.DataValidateException;

/**
 * 对于一些复杂的数据格式，例如奖励，消耗，可使用自定义验证器
 */
public class CustomValidator implements DataValidator {

    DataRepository dataRepository;

    public CustomValidator(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public void check(Class<?> clazz) throws DataValidateException {
        Container container = dataRepository.queryContainer(clazz, Container.class);
        if (container == null) {
            return;
        }
        container.validate();
        container.validate(dataRepository);
    }
}
