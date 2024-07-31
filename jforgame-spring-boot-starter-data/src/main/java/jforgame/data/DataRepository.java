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
     * @param <E>
     * @return
     */
    <E> E queryById(Class<E> clazz, Object id);

    /**
     * 读取指定文件的所有配置数据
     *
     * @param clazz
     * @param <E>
     * @return
     */
    <E> List<E> queryAll(Class<E> clazz);


    /**
     * 根据索引读取指定文件的配置数据
     *
     * @param clazz
     * @param name
     * @param index
     * @param <E>
     * @return
     */
    <E> List<E> queryByIndex(Class<E> clazz, String name, Object index);

    /**
     * 表格数据重载
     *
     * @param table
     */
    void reload(String table);


}
