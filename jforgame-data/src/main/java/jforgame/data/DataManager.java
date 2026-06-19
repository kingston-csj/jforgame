package jforgame.data;

import jforgame.commons.util.ClassScanner;
import jforgame.data.annotation.DataTable;
import jforgame.data.common.CommonContainer;
import jforgame.data.common.CommonData;
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
 * The only externally exposed API for data reading
 * Manages all configuration data uniformly, no longer one configuration file per configuration container
 * To implement secondary cache, simply extend {@link Container}, refer to {@link ResourceOptions#getContainerScanPath()} parameter
 */
public class DataManager implements DataRepository {

    private final Logger logger = LoggerFactory.getLogger(DataManager.class.getName());

    private final ResourceOptions options;

    private final DataReader dataReader;

    /**
     * Configuration table definitions, key is always lowercase table name
     */
    private final Map<String, TableDefinition> tableDefinitions = new HashMap<>();

    /**
     * Configuration container definitions, key is always lowercase table name
     */
    private final Map<String, Class<? extends Container>> containerDefinitions = new HashMap<>();

    private final ConcurrentMap<Class, Container> data = new ConcurrentHashMap<>();

    /**
     * Data validators for checking data integrity
     */
    private final List<DataValidator> validators = new LinkedList<>();

    public DataManager(ResourceOptions options, DataReader dataReader) {
        this.options = options;
        this.dataReader = dataReader;
        this.validators.add(new ForeignKeyValidator(this));
        this.validators.add(new CustomValidator(this));
    }

    /**
     * Initialize data
     * Scans all container classes and configuration table classes under the configured path
     * Loads configuration table data into memory
     */
    public void init() {
        if (!StringUtils.isEmpty(options.getContainerScanPath())) {
            Set<Class<?>> containers = ClassScanner.listAllSubclasses(options.getContainerScanPath(), Container.class);
            containers.forEach(c -> {
                // Container naming must be configuration filename + Container, e.g., if configuration table is common.csv, the corresponding Container is CommonContainer
                String name = c.getSimpleName().replace("Container", "").toLowerCase();
                containerDefinitions.put(name, (Class<? extends Container>) c);
            });
        }

        Set<Class<?>> classSet = ClassScanner.listClassesWithAnnotation(options.getTableScanPath(), DataTable.class);
        // Load common table by default
        classSet.add(CommonData.class);
        containerDefinitions.put(options.getCommonTableName(), CommonContainer.class);

        classSet.forEach(this::registerContainer);
        // Data validation
        dataCheck(classSet);
    }

    /**
     * Data integrity check, including foreign key constraint checks
     *
     * @param classSet the set of classes to check
     */
    private void dataCheck(Set<Class<?>> classSet) {
        logger.info("Starting data integrity check...");
        for (DataValidator validator : validators) {
            for (Class<?> clazz : classSet) {
                try {
                    validator.check(clazz);
                } catch (Exception e) {
                    logger.error("Data integrity check failed, class: {}", clazz.getSimpleName(), e);
                    throw new IllegalStateException("Data integrity check failed: " + clazz.getSimpleName(), e);
                }
            }
        }
        logger.info("Data integrity check completed");
    }


    /**
     * Register container by domain class
     * Automatically loads the corresponding configuration file
     *
     * @param table the class corresponding to the configuration table
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
        // Use lowercase uniformly
        tableName = tableName.toLowerCase();
        // Special handling for common table
        if (CommonData.class == table) {
            tableName = options.getCommonTableName();
        }
        tableDefinitions.put(tableName, definition);

        reload(tableName);
    }

    @Override
    public void reload(String table) {
        // Use lowercase uniformly
        table = table.toLowerCase();
        TableDefinition definition = tableDefinitions.get(table);
        if (definition == null) {
            throw new IllegalStateException(table + " not found");
        }
        try {
            Resource resource = new FileSystemResource(options.getLocation() + definition.getResourceTable() + options.getSuffix());
            List<?> records = new LinkedList<>();
            Container container = new Container<>();
            if (containerDefinitions.containsKey(table)) {
                container = containerDefinitions.get(table).newInstance();
            }
            try {
                records = dataReader.read(resource.getInputStream(), definition.getClazz());
            } catch (IOException e) {
                    if (table.equals(options.getCommonTableName())) {
                        // Allow projects to not use common table functionality
                        logger.info("Common table configuration is empty");
                    } else {
                        throw new IllegalStateException(String.format("cannot read %s data file", table));
                    }
                }

            container.inject(definition, records);
            // Cache secondary data
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
     * Returns all loaded configuration domain classes
     *
     * @return list of all loaded configuration classes
     */
    public Set<Class> getAllTables() {
        return data.keySet();
    }

}
