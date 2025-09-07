package jforgame.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Container<K extends Serializable & Comparable<K>, V> {

    protected final Map<K, V> data = new HashMap<>();

    /**
     * key is name@index and value is list of elements
     */
    protected final Map<String, List<V>> indexMapper = new HashMap<>();

    public void inject(TableDefinition definition, List<V> records) {
        records.forEach(row -> {
            @SuppressWarnings("unchecked")
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

    /**
     * 数据校验
     * 该接口会在所有数据加载完成后调用
     * 可以在此接口中关联其他配置表进行校验
     * @throws RuntimeException 校验失败抛出异常，启服加载时会终止程序启动
     */
    public void validate() {

    }

    public List<V> getRecordsBy(String name, Object index) {
        String key = indexKey(name, index);
        return indexMapper.getOrDefault(key, Collections.EMPTY_LIST);
    }

    public List<V> getAllRecords() {
        return new ArrayList<>(data.values());
    }

    /**
     * 获取所有主键id
     *
     * @return 该配置表所有主键的集合
     */
    public Set<K> getAllKeys() {
        return new TreeSet<>(data.keySet());
    }

    public V getRecord(K id) {
        return data.get(id);
    }

    private String indexKey(String name, Object index) {
        return name + "@" + index;
    }

}