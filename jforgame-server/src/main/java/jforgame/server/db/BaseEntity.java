package jforgame.server.db;

import jforgame.orm.cache.AbstractCacheable;
import jforgame.socket.actor.Actor;

import java.io.Serializable;

/**
 * abstract base class for db entity
 *
 * @author kinson
 */
@SuppressWarnings("serial")
public abstract class BaseEntity<Id extends Comparable & Serializable> extends AbstractCacheable
        implements Serializable, Actor {

    /**
     * 实体的主键属性最好定义为包装类型，防止属性与getter/setter方法类型不匹配
     * TODO 增加起服验证
     * entity id
     * @return
     */
    public abstract Id getId();

    /**
     * init hook
     */
    public void doAfterInit() {
    }

    /**
     * save hook
     */
    public void doBeforeSave() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getId().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        BaseEntity other = (BaseEntity) obj;
        if (getId() != other.getId()) {
            return false;
        }
        return true;
    }

}
