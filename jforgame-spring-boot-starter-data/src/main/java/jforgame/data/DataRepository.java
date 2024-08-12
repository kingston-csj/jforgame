package jforgame.data;

import java.util.List;

/**
 * 配置读取仓库（只读）
 */
public interface DataRepository {


    /**
     * 根据主键读取指定文件的配置数据
     *
     * @param clazz
     * @param id
     * @return 指定id记录
     */
    <E> E queryById(Class<E> clazz, Object id);

    /**
     * 读取指定文件的所有配置数据
     *
     * @param clazz
     * @return 所有记录
     */
    <E> List<E> queryAll(Class<E> clazz);


    /**
     * 根据索引读取指定文件的配置数据
     *
     * @param clazz 配置类class
     * @param index 索引名称
     * @return 指定索引所有数据
     */
    <E> List<E> queryByIndex(Class<E> clazz, String name, Object index);

    /**
     * 表格数据重载
     *
     * @param table 表名称
     */
    void reload(String table);


}