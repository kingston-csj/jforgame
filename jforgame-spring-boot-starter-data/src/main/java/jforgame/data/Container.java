package jforgame.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Container<K extends Serializable & Comparable<K>, V> {

    protected final Map<K, V> data = new HashMap<>();

    /**
     * name@index --> List<V>
     */
    protected final Map<String, List<V>> indexMapper = new HashMap<>();

    public void inject(TableDefinition definition, List<V> records) {
        records.forEach(row -> {
            K id = (K) definition.getIdMeta().getValue(row);
            data.put(id, row);

            for (Map.Entry<String, IndexMeta> entry : definition.getIndexMetaMap().entrySet()) {
                IndexMeta indexMeta = entry.getValue();
                String index = indexMeta.getName();
                Object val = indexMeta.getValue(row);
                String key = indexKey(index, val);
                indexMapper.putIfAbsent(key, new ArrayList<>());
                indexMapper.get(key).add(row);
            }
        });
    }

    /**
     * 初始化二级缓存
     */
    public void init() {

    }

    public List<V> getRecordsBy(String name, Object index) {
        String key = indexKey(name, index);
        return indexMapper.getOrDefault(key, Collections.EMPTY_LIST);
    }

    public List<V> getAllRecords() {
        return new ArrayList<>(data.values());
    }

    public V getRecord(K id) {
        return data.get(id);
    }

    private String indexKey(String name, Object index) {
        return name + "@" + index;
    }

}