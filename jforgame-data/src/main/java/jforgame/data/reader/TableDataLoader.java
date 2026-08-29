package jforgame.data.reader;

import jforgame.data.ResourceOptions;
import jforgame.data.TableDefinition;

import java.util.List;

/**
 * Strategy interface for loading table configuration records.
 * <p>
 * Decouples data fetching logic from DataManager.
 * Implementations can load records from local file, database, remote service or other custom sources.
 * </p>
 */
public interface TableDataLoader {

    /**
     * Load table records from custom data source.
     * The source can be local file, database, remote http and so on.
     *
     * @param definition metadata definition of target table
     * @param options global resource configuration options
     * @param <E> type of target record entity
     * @return loaded record list, empty list is allowed
     * @throws Exception throw when load failed and cannot be recovered
     */
    <E> List<E> load(TableDefinition definition, ResourceOptions options) throws Exception;
}
