package com.kingston.cache;

/**
 * 可持久化的
 * @author kingston
 */
public interface Persistable<K, V> {
	
	/**
	 * 能从数据库获取bean
	 * @param k 查询主键
	 * @return  持久化对象
	 * @throws Exception
	 */
    V load(K k) throws Exception;
    
//    /**
//     * 将对象序列号到数据库
//     * @param k
//     * @param v
//     * @throws PersistenceException
//     */
//    void save(K k, V v) throws Exception;
    
}
