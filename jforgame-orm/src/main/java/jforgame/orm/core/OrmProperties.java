package jforgame.orm.core;

/**
 * ORM框架配置类
 * 基础配置类，不依赖Spring
 */
public class OrmProperties {

    /**
     * DDL自动策略
     * create:每次启动时删除所有现有表，然后根据实体类重新创建表结构
     * update:启动时根据实体类自动更新表结构（新增字段、索引等，不删除现有字段或表）
     * validate:启动时校验实体类与表结构是否一致，不一致则抛出异常，不做任何修改
     * none:不做任何操作
     */
    private String ddlAuto = "update";

    /**
     * 实体类路径
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
