package jforgame.orm.core;

/**
 * 数遍库实体状态
 */
public enum DbStatus {

    /**
     * 无需入库
     */
    NORMAL,
    /**
     * 待更新
     */
    UPDATE,
    /**
     * 待插入
     */
    INSERT,
    /**
     * 待删除
     */
    DELETE,

}
