package jforgame.data;

import jforgame.commons.ClassScanner;
import jforgame.data.annotation.PTable;
import jforgame.data.reader.DataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 数据读取对外暴露的唯一API
 */
public class DataManager implements DataRepository {

    private Logger logger = LoggerFactory.getLogger(DataManager.class.getName());

    @Autowired
    private ResourceProperties properties;

    @Autowired
    private DataReader dataReader;

    private final Map<String, TableDefinition> tableDefinitions = new HashMap<>();

    private final ConcurrentMap<Class, Container> data = new ConcurrentHashMap<>();

    public void init() {
        Set<Class<?>> classSet = ClassScanner.listClassesWithAnnotation(properties.getScanPath(), PTable.class);
        classSet.forEach(this::registerContainer);
    }

    public void registerContainer(Class<?> table) {
        if (table == null) {
            throw new NullPointerException("");
        }
        if (table.getAnnotation(PTable.class) == null) {
            throw new IllegalStateException(table.getName() + "没有PTable注解");
        }
        TableDefinition definition = new TableDefinition(table);
        String tableName = table.getSimpleName().toLowerCase();
        tableDefinitions.put(tableName, definition);

        reload(tableName);
    }

    @Override
    public void reload(String table) {
        table = table.toLowerCase();
        TableDefinition definition = tableDefinitions.get(table);
        if (definition == null) {
            throw new IllegalStateException(table + "不属于配置表");
        }
        try {
            Resource resource = new ClassPathResource(properties.getLocation() + table + properties.getSuffix());
            List<?> records = null;
            try {
                records = dataReader.read(resource.getInputStream(), definition.getClazz());
            } catch (IOException e) {
                throw new IllegalStateException(table + "无法读取");
            }
            Container container = new Container<>();
            container.inject(definition, records);

            data.put(definition.getClazz(), container);
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(table + "配置异常", e);
        }
    }

    @Override
    public <E> E queryById(Class<E> clazz, Object id) {
        if (!data.containsKey(clazz)) {
            return null;
        }
        return (E) data.get(clazz).getRecord((Serializable) id);
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
        return data.get(clazz).getRecordsBy(name, index);
    }

}
