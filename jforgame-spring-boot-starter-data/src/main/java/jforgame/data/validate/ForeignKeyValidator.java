package jforgame.data.validate;

import jforgame.data.Container;
import jforgame.data.DataRepository;
import jforgame.data.annotation.DataTable;
import jforgame.data.annotation.ForeignKey;
import jforgame.data.exception.DataValidateException;
import jforgame.data.exception.ForeignKeyConstraintException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 检查单表所有外键约束
 */
public class ForeignKeyValidator implements DataValidator {

    private final Logger logger = LoggerFactory.getLogger(ForeignKeyValidator.class);

    DataRepository dataManager;

    public ForeignKeyValidator(DataRepository dataRepository) {
        this.dataManager = dataRepository;
    }

    @Override
    public void check(Class<?> clazz) throws DataValidateException {
        Container container = dataManager.queryContainer(clazz, Container.class);
        if (container == null) {
            logger.warn("容器未找到，跳过外键检查: {}", clazz.getSimpleName());
            return;
        }

        // 获取所有带外键注解的字段
        List<Field> foreignKeyFields = getForeignKeyFields(clazz);
        if (foreignKeyFields.isEmpty()) {
            return;
        }
        logger.debug("检查类 {} 的外键约束，外键字段数量: {}", clazz.getSimpleName(), foreignKeyFields.size());

        for (Object record : container.getAllRecords()) {
            for (Field field : foreignKeyFields) {
                try {
                    checkForeignKeyField(record, field);
                } catch (Exception e) {
                    String errorMsg = String.format("外键检查失败 - 类: %s, 记录ID: %s, 字段: %s",
                            clazz.getSimpleName(), getRecordId(record), field.getName());
                    logger.error(errorMsg, e);
                    throw new ForeignKeyConstraintException(errorMsg, e);
                }
            }
        }
    }

    /**
     * 获取记录ID
     *
     * @param record 记录对象
     * @return 记录ID
     */
    private Object getRecordId(Object record) {
        try {
            // 尝试通过getId()方法获取ID
            Method getIdMethod = record.getClass().getMethod("getId");
            return getIdMethod.invoke(record);
        } catch (Exception e) {
            // 如果无法获取ID，返回null
            return null;
        }
    }

    /**
     * 获取类中所有带外键注解的字段
     *
     * @param clazz 要检查的类
     * @return 外键字段列表
     */
    private List<Field> getForeignKeyFields(Class<?> clazz) {
        List<Field> foreignKeyFields = new ArrayList<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(ForeignKey.class)) {
                field.setAccessible(true);
                foreignKeyFields.add(field);
            }
        }

        return foreignKeyFields;
    }

    /**
     * 检查单个外键字段
     *
     * @param record 当前记录
     * @param field  外键字段
     */
    private void checkForeignKeyField(Object record, Field field) throws DataValidateException {
        ForeignKey foreignKey = field.getAnnotation(ForeignKey.class);
        Class<?> referencedClass = foreignKey.refer();

        // 获取外键字段值
        Object foreignKeyValue = getFieldValue(record, field);
        if (foreignKeyValue == null) {
            String errorMsg = String.format("外键约束违反 - 类: %s, 记录ID: %s, 字段: %s, 外键值: null, 引用表: %s",
                    record.getClass().getSimpleName(), getRecordId(record), field.getName(),
                    getTableName(referencedClass));
            throw new ForeignKeyConstraintException(errorMsg);
        }

        Container container = dataManager.queryContainer(referencedClass, Container.class);
        Set<Object> referencedIds = container.getAllKeys();
        // 检查外键值是否存在于引用表中
        if (!referencedIds.contains(foreignKeyValue)) {
            String errorMsg = String.format("外键约束违反 - 类: %s, 记录ID: %s, 字段: %s, 外键值: %s, 引用表: %s",
                    record.getClass().getSimpleName(), getRecordId(record), field.getName(),
                    foreignKeyValue, getTableName(referencedClass));
            throw new ForeignKeyConstraintException(errorMsg);
        }
    }

    private String getTableName(Class<?> tableClass) {
        if (tableClass.isAnnotationPresent(DataTable.class)) {
            return tableClass.getAnnotation(DataTable.class).name();
        }
        return tableClass.getSimpleName();
    }

    /**
     * 获取字段值
     *
     * @param record 记录对象
     * @param field  字段
     * @return 字段值
     */
    private Object getFieldValue(Object record, Field field) {
        try {
            return field.get(record);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("无法访问字段: " + field.getName(), e);
        }
    }
}
