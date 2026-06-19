package jforgame.orm.core;

/**
 * Database entity status
 */
public enum DbStatus {

    /**
     * No need to persist
     */
    NORMAL,
    /**
     * Needs update
     */
    UPDATE,
    /**
     * Needs insert
     */
    INSERT,
    /**
     * Needs delete. Once an entity is deleted, it cannot be re-inserted into the database.
     */
    DELETE,

    ;
}
