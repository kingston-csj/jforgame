package jforgame.data;

import jforgame.data.exception.DataValidateException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Container<K extends Serializable & Comparable<K>, V> {

    // Use LinkedHashMap to preserve the order of records from top to bottom in the configuration file
    protected final Map<K, V> data = new LinkedHashMap<>();

    /**
     * key is name@index and value is list of elements
     */
    protected final Map<String, List<V>> indexMapper = new HashMap<>();

    public void inject(TableDefinition definition, List<V> records) {
        Set<String> keys = new HashSet<>();
        records.forEach(row -> {
            @SuppressWarnings("unchecked")
            K id = (K) definition.getIdMeta().getValue(row);
            V prev = data.put(id, row);
            if (prev != null) {
                throw new IllegalStateException("Configuration table [" + definition.getResourceTable() + "] primary key duplicate, id: " + id);
            }

            for (Map.Entry<String, IndexMeta> entry : definition.getIndexMetaMap().entrySet()) {
                IndexMeta indexMeta = entry.getValue();
                String index = indexMeta.getName();
                Object val = indexMeta.getValue(row);
                String key = indexKey(index, val);
                if (indexMeta.isUnique()) {
                    if (keys.contains(key)) {
                        throw new IllegalStateException("Configuration table " + definition.getResourceTable() + " unique index " + index + " duplicate, value " + val);
                    }
                    keys.add(key);
                }
                indexMapper.putIfAbsent(key, new ArrayList<>());
                indexMapper.get(key).add(row);
            }
        });
    }

    /**
     * Initialize secondary cache
     */
    public void afterLoad() {

    }

    /**
     * Data validation
     * This method is called after all data has been loaded
     * Can associate other configuration tables for validation here
     *
     * @throws RuntimeException throws exception if validation fails, which will terminate application startup
     */
    public void validate(DataRepository dataRepository) throws DataValidateException {

    }


    public List<V> getRecordsByIndex(String name, Object index) {
        String key = indexKey(name, index);
        return indexMapper.getOrDefault(key, Collections.EMPTY_LIST);
    }

    public V getUniqueRecordByIndex(String name, Object index) {
        String key = indexKey(name, index);
        List<V> records = indexMapper.get(key);
        if (records == null || records.isEmpty()) {
            return null;
        }
        return records.get(0);
    }

    /**
     * Gets all records
     *
     * @return a reference copy of all records in this configuration table
     */
    public List<V> getAllRecords() {
        return new ArrayList<>(data.values());
    }

    /**
     * Gets all primary key IDs
     *
     * @return the set of all primary keys in this configuration table
     */
    public Set<K> getAllKeys() {
        return new TreeSet<>(data.keySet());
    }

    /**
     * Gets record by ID
     *
     * @param id the primary key ID
     * @return the record
     */
    public V getRecordById(K id) {
        return data.get(id);
    }

    private String indexKey(String name, Object index) {
        return name + "@" + index;
    }

}