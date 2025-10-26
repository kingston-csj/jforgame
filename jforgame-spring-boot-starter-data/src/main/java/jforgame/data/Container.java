package jforgame.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
        Set<String> keys = new HashSet<>();
        records.forEach(row -> {
            @SuppressWarnings("unchecked")
            K id = (K) definition.getIdMeta().getValue(row);
            V prev = data.put(id, row);
            if (prev != null) {
                throw new IllegalStateException("配置表[" + definition.getResourceTable() + "]主键重复，id为：" + id);
            }

            for (Map.Entry<String, IndexMeta> entry : definition.getIndexMetaMap().entrySet()) {
                IndexMeta indexMeta = entry.getValue();
                String index = indexMeta.getName();
                Object val = indexMeta.getValue(row);
                String key = indexKey(index, val);
                if (indexMeta.isUnique()) {
                    if (keys.contains(key)) {
                        throw new IllegalStateException("配置表" + definition.getResourceTable() + "的唯一索引" + index + "重复，值为" + val);
                    }
                    keys.add(key);
                }
                indexMapper.putIfAbsent(key, new ArrayList<>());
                indexMapper.get(key).add(row);
            }
        });
    }

    /**
     * 初始化二级缓存
     */
    public void afterLoad() {

    }

    /**
     * 数据校验
     * 该接口会在所有数据加载完成后调用
     * 可以在此接口中关联其他配置表进行校验
     * 该方法缺少参数，在验证的时候无法关联其他配置表进行校验
     * 使用{@link #validate(DataRepository)}替代
     * @throws RuntimeException 校验失败抛出异常，启服加载时会终止程序启动
     */
    @Deprecated
    public void validate() {

    }

    /**
     * 数据校验
     * 该接口会在所有数据加载完成后调用
     * 可以在此接口中关联其他配置表进行校验
     *
     * @throws RuntimeException 校验失败抛出异常，启服加载时会终止程序启动
     */
    public void validate(DataRepository dataRepository) {

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
     * 获取所有记录
     *
     * @return 该配置表所有记录的集合(引用拷贝)
     */
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

    /**
     * 根据id获取记录
     *
     * @param id 主键id
     * @return 记录
     */
    public V getRecordById(K id) {
        return data.get(id);
    }

    private String indexKey(String name, Object index) {
        return name + "@" + index;
    }

}