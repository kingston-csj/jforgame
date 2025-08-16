package jforgame.orm.entity;

import jforgame.orm.core.DbStatus;

/**
 * 拥有各种db状态的对象
 */
public abstract class Stateful {

    /**
     * 当前实体对象的db状态
     */
    protected DbStatus status = DbStatus.NORMAL;

    /**
     * 返回当前的db状态
     *
     * @return 前的db状态
     */
    public abstract DbStatus getStatus();

    /**
     * 判断实体是否为正常状态，无需更新到数据库
     *
     * @return 如果实体状态为正常，返回true
     */
    public abstract boolean isNormal();

    /**
     * 判断实体是否为新增状态（待插入到数据库）
     *
     * @return 如果实体状态为新增，返回true
     */
    public abstract boolean isNew();

    /**
     * 判断实体是否为待更新状态
     *
     * @return 如果实体状态待更新，返回true
     */
    public abstract boolean isModified();

    /**
     * 判断实体是否处于逻辑删除状态（已标记为删除，但尚未物理删除）
     *
     * @return 如果实体已被逻辑删除，返回true
     */
    public abstract boolean isSoftDeleted();

    /**
     * 标记实体为新增状态（准备插入到数据库）
     */
    public abstract void markAsNew();

    /**
     * 标记实体为修改状态（准备更新到数据库）
     */
    public abstract void markAsModified();

    /**
     * 标记实体为逻辑删除状态（准备从数据库物理删除）
     */
    public abstract void markAsSoftDeleted();


}
