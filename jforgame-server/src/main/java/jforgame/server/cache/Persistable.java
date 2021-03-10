package jforgame.server.cache;

/**
 * 可持久化的
 * @author kinson
 */
public interface Persistable<K, V> {
	
	/**
	 * 能从数据库获取bean
	 * @param k 查询主键
	 * @return  持久化对象
	 * @throws Exception
	 */
    V load(K k) throws Exception;
    
    /**
     * 异步将对象持久化到数据库
     * @param v
     */
     void save(V v);
    
}
