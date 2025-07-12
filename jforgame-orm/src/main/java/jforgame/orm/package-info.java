/**
 * 自定义ORM框架，有以下特点
 * 1. 支持多数据源
 * 2. 支持全字段更新与增量字段更新
 * 3. 支持ORM属性数据转换 {@link jforgame.orm.converter.Convert} {@link jforgame.orm.converter.AttributeConverter}
 * 4. 支持数据库schema自动建表，增加字段，不会自动删除字段，自动更新字段类型
 * 5. 提供多种异步持久化容器  {@link jforgame.orm.asyncdb.PersistContainer} {@link jforgame.orm.asyncdb.SavingStrategy}
 */
package jforgame.orm;