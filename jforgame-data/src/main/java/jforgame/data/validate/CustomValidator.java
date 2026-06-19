package jforgame.data.validate;

import jforgame.data.Container;
import jforgame.data.DataRepository;
import jforgame.data.exception.DataValidateException;

/**
 * For complex data formats such as rewards and costs, custom validators can be used
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
        container.validate(dataRepository);
    }
}
