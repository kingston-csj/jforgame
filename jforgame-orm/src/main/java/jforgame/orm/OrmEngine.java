package jforgame.orm;

import java.util.Set;

import javax.sql.DataSource;

import jforgame.commons.ClassScanner;
import jforgame.orm.core.OrmProcessor;
import jforgame.orm.core.OrmProperties;
import jforgame.orm.ddl.SchemaAction;
import jforgame.orm.ddl.SchemaCreator;
import jforgame.orm.ddl.SchemaMigrator;
import jforgame.orm.ddl.SchemaValidator;
import jforgame.orm.entity.BaseEntity;

public class OrmEngine {

    /**
     * 启动OrmEngine
     */
    public static void run(OrmProperties properties, DataSource dataSource) throws Exception {
        // 初始化orm框架
        OrmProcessor.INSTANCE.initOrmBridges(properties.getEntityPath());
        // 获取所有实体类
        Set<Class<?>> codeTables = ClassScanner.listAllSubclasses(properties.getEntityPath(), BaseEntity.class);

        // 根据配置的DDL策略执行相应的操作
        executeDdlSchema(dataSource, codeTables, properties.getDdlAuto());
    }

    private static void executeDdlSchema(DataSource dataSource, Set<Class<?>> codeTables, String ddlAuto) throws Exception {
        if (SchemaAction.UPDATE.name().equalsIgnoreCase(ddlAuto)) {
            // 数据库自动更新schema（增量更新）
            new SchemaMigrator().doExecute(dataSource.getConnection(), codeTables);
        } else if (SchemaAction.CREATE.name().equalsIgnoreCase(ddlAuto)) {
            // 清空数据库并重新建表
            new SchemaCreator().doExecute(dataSource.getConnection(), codeTables);
        } else if (SchemaAction.VALIDATE.name().equalsIgnoreCase(ddlAuto)) {
            // 校验表结构
            new SchemaValidator().doExecute(dataSource.getConnection(), codeTables);
        }
    }
}
