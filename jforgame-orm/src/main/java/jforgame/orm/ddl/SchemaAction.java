package jforgame.orm.ddl;

public enum SchemaAction {

    /**
     * Do nothing, corresponds to "none"
     */
    NONE,
    /**
     * Validate consistency between entity classes and table structure on startup, throw exception if inconsistent, do not modify anything, corresponds to "validate"
     */
    VALIDATE,

    /**
     * Auto update table structure based on entity classes on startup (add fields, indexes, etc., do not delete existing fields or tables), corresponds to "update"
     */
    UPDATE,
    /**
     * Drop all existing tables on every startup, then recreate table structure based on entity classes, corresponds to "create"
     */
    CREATE,
}
