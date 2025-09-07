package jforgame.data;

import java.io.Serializable;
import java.util.List;

/**
 * 配置读取仓库（只读）
 */
public interface DataRepository {


    /**
     * 查询配置容器
     * @param tableClass 配置类
     * @param containerClass Container子类
     * @param  <T> Container子类
     * @return containerClass container类
     */
    <T extends Container> T queryContainer(Class<?> tableClass, Class<T> containerClass);

    /**
     * 根据主键读取指定文件的配置数据
     *
     * @param clazz　配置类
     * @param id　配置表主键
     * @return 指定id记录
     */
    <E> E queryById(Class<E> clazz, Serializable id);

    /**
     * 读取指定文件的所有配置数据
     *
     * @param clazz　配置类
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