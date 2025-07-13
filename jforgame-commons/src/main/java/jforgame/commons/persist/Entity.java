package jforgame.commons.persist;

import java.io.Serializable;

/**
 * 数据表记录实体接口
 *
 * @param <ID> 主键类型
 */
public interface Entity<ID extends Serializable & Comparable<ID>> {

    /**
     * 获取实体主键
     *
     * @return
     */
    ID getId();

    /**
     * 获取主键的字符串表示
     *
     * @return
     */
    default String getKey() {
        return getClass().getSimpleName() + "@" + getId().toString();
    }

}
