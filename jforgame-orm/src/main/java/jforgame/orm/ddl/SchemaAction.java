package jforgame.orm.ddl;

public enum SchemaAction {

    /**
     * 不做任何操作, 对应 "none"
     */
    NONE,
    /**
     * 启动时校验实体类与表结构是否一致，不一致则抛出异常，不做任何修改，对应 "validate"
     */
    VALIDATE,

    /**
     * 启动时根据实体类自动更新表结构（新增字段、索引等，不删除现有字段或表），对应 "update"
     */
    UPDATE,
    /**
     * 每次启动时删除所有现有表，然后根据实体类重新创建表结构，对应 "create"
     */
    CREATE,
}
