package jforgame.commons.persist;

import java.io.Serializable;

/**
 * Data table record entity interface
 *
 * @param <ID> primary key type
 */
public interface Entity<ID extends Serializable & Comparable<ID>> {

    /**
     * Get entity primary key
     *
     * @return primary key value
     */
    ID getId();

    /**
     * Get string representation of primary key
     *
     * @return string representation of primary key
     */
    default String getKey() {
        return getClass().getSimpleName() + "@" + getId().toString();
    }

}
