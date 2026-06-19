package jforgame.data;

import java.io.Serializable;
import java.util.List;

/**
 * Configuration reading repository (read-only)
 */
public interface DataRepository {


    /**
     * Query configuration container
     *
     * @param tableClass     configuration class
     * @param containerClass Container subclass
     * @param <T>            Container subclass type
     * @return containerClass container
     */
    <T extends Container> T queryContainer(Class<?> tableClass, Class<T> containerClass);

    /**
     * Read configuration data from specified file by primary key
     *
     * @param clazz configuration class
     * @param <E>   configuration class type
     * @param id    configuration table primary key
     * @return record with specified id
     */
    <E> E queryById(Class<E> clazz, Serializable id);

    /**
     * Read all configuration data from specified file
     *
     * @param clazz configuration class
     * @param <E>   configuration class type
     * @return all records
     */
    <E> List<E> queryAll(Class<E> clazz);


    /**
     * Read configuration data list from specified file by index
     *
     * @param clazz configuration class
     * @param <E>   configuration class type
     * @param name  index name
     * @param index index value
     * @return all data with specified index
     */
    <E> List<E> queryByIndex(Class<E> clazz, String name, Object index);

    /**
     * Read configuration data from specified file by unique index
     *
     * @param clazz configuration class
     * @param <E>   configuration class type
     * @param name  index name
     * @param index index value
     * @return indexed data
     */
    <E> E queryByUniqueIndex(Class<E> clazz, String name, Object index);

    /**
     * Reload table data
     *
     * @param table table name
     */
    void reload(String table);


}