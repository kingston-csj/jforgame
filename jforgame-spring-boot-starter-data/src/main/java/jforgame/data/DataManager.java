package jforgame.data;

import jforgame.commons.util.ClassScanner;
import jforgame.data.annotation.DataTable;
import jforgame.data.common.CommonContainer;
import jforgame.data.common.CommonData;
import jforgame.data.exception.DataValidateException;
import jforgame.data.reader.DataReader;
import jforgame.data.validate.CustomValidator;
import jforgame.data.validate.DataValidator;
import jforgame.data.validate.ForeignKeyValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 数据读取对外暴露的唯一API
 * 对所有的配置数据作统一管理，不再一个配置文件对应一个配置容器
 * 如果需要实现二级缓存，只需继承{@link Container}即可，参考{@link ResourceProperties#getContainerScanPath()}参数
 */
public class DataManager implements DataRepository {

    private final Logger logger = LoggerFactory.getLogger(DataManager.class.getName());

    private final ResourceProperties properties;

    private final DataReader dataReader;

    /**
     * 配置表定义, key统一为表名称小写
     */
    private final Map<String, TableDefinition> tableDefinitions = new HashMap<>();

    /**
     * 配置容器定义, key统一为表名称小写
     */
    private final Map<String, Class<? extends Container>> containerDefinitions = new HashMap<>();

    private final ConcurrentMap<Class, Container> data = new ConcurrentHashMap<>();

    /**
     * 数据校验器，用于检查数据的完整性
     */
    private final List<DataValidator> validators = new LinkedList<>();

    public DataManager(ResourceProperties properties, DataReader dataReader) {
        this.properties = properties;
        this.dataReader = dataReader;
        this.validators.add(new ForeignKeyValidator(this));
        this.validators.add(new CustomValidator(this));
    }

    /**
     * 初始化数据
     * 会扫描配置路径下所有的容器类和配置表类
     * 并加载配置表数据到内存中
     */
    public void init() {
        if (!StringUtils.isEmpty(properties.getContainerScanPath())) {
            Set<Class<?>> containers = ClassScanner.listAllSubclasses(properties.getContainerScanPath(), Container.class);
            containers.forEach(c -> {
                // container命名必须以配置文件名+Container,例如配置表为common.csv，则对应的Container命名为CommonContainer
                String name = c.getSimpleName().replace("Container", "").toLowerCase();
                containerDefinitions.put(name, (Class<? extends Container>) c);
            });
        }

        Set<Class<?>> classSet = ClassScanner.listClassesWithAnnotation(properties.getTableScanPath(), DataTable.class);
        // 默认加载common表
        classSet.add(CommonData.class);
        containerDefinitions.put(properties.getCommonTableName(), CommonContainer.class);

        classSet.forEach(this::registerContainer);
        dataCheck(classSet);
    }

    /**
     * 数据完整性检查，包括外键约束检查
     *
     * @param classSet 需要检查的类集合
     */
    private void dataCheck(Set<Class<?>> classSet) {
        logger.info("开始数据完整性检查...");
        for (DataValidator validator : validators) {
            try {
                for (Class<?> clazz : classSet) {
                    try {
                        validator.check(clazz);
                    } catch (Exception e) {
                        logger.error("数据完整性检查失败，类: {}", clazz.getSimpleName(), e);
                        throw new DataValidateException("数据完整性检查失败: " + clazz.getSimpleName(), e);
                    }
                }
            } catch (Exception e) {
                logger.error("数据完整性检查失败", e);
                throw new IllegalStateException("数据完整性检查失败", e);
            }
        }
        logger.info("数据完整性检查完成");
    }


    /**
     * 根据领域类注册容器
     * 会自动加载对应的配置文件
     *
     * @param table　配置表对应的类
     */
    public void registerContainer(Class<?> table) {
        if (table == null) {
            throw new NullPointerException("");
        }
        if (table.getAnnotation(DataTable.class) == null) {
            throw new IllegalStateException(table.getName() + "DataTable annotation not found");
        }
        TableDefinition definition = new TableDefinition(table);
        String tableName = definition.getResourceTable();
        // 统一使用小写
        tableName = tableName.toLowerCase();
        // 特殊处理common表
        if (CommonData.class == table) {
            tableName = properties.getCommonTableName();
        }
        tableDefinitions.put(tableName, definition);

        reload(tableName);
    }

    @Override
    public void reload(String table) {
        // 统一使用小写
        table = table.toLowerCase();
        TableDefinition definition = tableDefinitions.get(table);
        if (definition == null) {
            throw new IllegalStateException(table + " not found");
        }
        try {
            Resource resource = new FileSystemResource(properties.getLocation() + definition.getResourceTable() + properties.getSuffix());
            List<?> records = new LinkedList<>();
            Container container = new Container<>();
            if (containerDefinitions.containsKey(table)) {
                container = containerDefinitions.get(table).newInstance();
            }
            try {
                records = dataReader.read(resource.getInputStream(), definition.getClazz());
            } catch (IOException e) {
                if (table.equals(properties.getCommonTableName())) {
                    // 允许项目不使用common表相关功能
                    logger.info("common表配置为空");
                } else {
                    throw new IllegalStateException(String.format("cannot read %s data file", table));
                }
            }

            container.inject(definition, records);
            // 二级缓存数据
            container.afterLoad();

            data.put(definition.getClazz(), container);
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(table + " read failed ", e);
        }
    }

    @Override
    public <T extends Container> T queryContainer(Class<?> tableClass, Class<T> containerClass) {
        return (T) data.get(tableClass);
    }

    @Override
    public <E> E queryById(Class<E> clazz, Serializable id) {
        if (!data.containsKey(clazz)) {
            return null;
        }
        return (E) data.get(clazz).getRecordById(id);
    }

    @Override
    public <E> List<E> queryAll(Class<E> clazz) {
        if (!data.containsKey(clazz)) {
            return Collections.EMPTY_LIST;
        }
        return data.get(clazz).getAllRecords();
    }

    @Override
    public <E> List<E> queryByIndex(Class<E> clazz, String name, Object index) {
        if (!data.containsKey(clazz)) {
            return Collections.EMPTY_LIST;
        }
        return data.get(clazz).getRecordsByIndex(name, index);
    }

    @Override
    public <E> E queryByUniqueIndex(Class<E> clazz, String name, Object index) {
        if (!data.containsKey(clazz)) {
            return null;
        }
        return (E) data.get(clazz).getUniqueRecordByIndex(name, index);
    }

    /**
     * 返回已加载的所有配置领域类
     *
     * @return 所有已加载的配置类列表
     */
    public Set<Class> getAllTables() {
        return data.keySet();
    }

}
