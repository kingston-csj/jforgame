package jforgame.orm.core;

/**
 * ORM framework configuration class.
 * Basic configuration class, does not depend on Spring.
 */
public class OrmProperties {

    /**
     * DDL auto strategy
     * create: Drop all existing tables on every startup, then recreate table structure based on entity classes
     * update: Auto update table structure based on entity classes on startup (add fields, indexes, etc., do not delete existing fields or tables)
     * validate: Validate consistency between entity classes and table structure on startup, throw exception if inconsistent, do not modify anything
     * none: Do nothing
     * Default is update
     */
    private String ddlAuto = "update";

    /**
     * Entity class package path. Does not support multiple paths.
     * Example: com.jforgame.orm.entity
     */
    private String entityPath;

    public String getDdlAuto() {
        return ddlAuto;
    }

    public void setDdlAuto(String ddlAuto) {
        this.ddlAuto = ddlAuto;
    }

    public String getEntityPath() {
        return entityPath;
    }

    public void setEntityPath(String entityPath) {
        this.entityPath = entityPath;
    }
}
