package jforgame.data;

import jforgame.commons.ClassScanner;
import jforgame.data.annotation.DataTable;
import jforgame.data.reader.DataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

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
 * 对所有的配置数据作统一管理，不再一个配置文件对应一个配置容器
 * 如果需要实现二级缓存，只需继承{@link Container}即可，参考{@link ResourceProperties#getContainerScanPath()}参数
 */
public class DataManager implements DataRepository {

    private final Logger logger = LoggerFactory.getLogger(DataManager.class.getName());

    private final ResourceProperties properties;

    private final DataReader dataReader;

    private final Map<String, TableDefinition> tableDefinitions = new HashMap<>();

    private final Map<String, Class<? extends Container>> containerDefinitions = new HashMap<>();

    private final ConcurrentMap<Class, Container> data = new ConcurrentHashMap<>();

    public DataManager(ResourceProperties properties, DataReader dataReader) {
        this.properties = properties;
        this.dataReader = dataReader;
    }

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
        classSet.forEach(this::registerContainer);
    }

    /**
     * 根据领域类注册容器
     * 会自动加载对应的配置文件
     * @param table
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
        tableDefinitions.put(tableName, definition);

        reload(tableName);
    }

    @Override
    public void reload(String table) {
        table = table.toLowerCase();
        TableDefinition definition = tableDefinitions.get(table);
        if (definition == null) {
            throw new IllegalStateException(table + " not found");
        }
        try {
            Resource resource = new FileSystemResource(properties.getLocation() + table + properties.getSuffix());
            List<?> records = null;
            try {
                records = dataReader.read(resource.getInputStream(), definition.getClazz());
            } catch (IOException e) {
                throw new IllegalStateException(String.format("cannot read %s data file", table));
            }
            Container container = new Container<>();
            if (containerDefinitions.containsKey(table)) {
                container = containerDefinitions.get(table).newInstance();
            }
            container.inject(definition, records);
            // 二级缓存数据
            container.init();

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
        return (E) data.get(clazz).getRecord(id);
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

    /**
     * 返回已加载的所有配置领域类
     * @return
     */
    public Set<Class> getAllTables() {
        return data.keySet();
    }

}
