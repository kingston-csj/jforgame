/**
 * 游戏领域orm的特点
 * 单表操作：每个操作只涉及一个表，不需要事务保证一致性
 * 缓存优先：数据先写入内存缓存，保证响应速度
 * 异步持久化：定期批量写入数据库，提高性能
 * 最终一致性：通过其他机制保证数据最终一致性
 * 自定义ORM框架，有以下特点
 * 1. 提供类似ApacheDbUtils的API，超级轻量级，完美适配游戏服务器的orm特点
 * 2. 支持多数据源
 * 3. 支持全字段更新与增量字段更新
 * 4. 支持ORM属性数据转换 {@link javax.persistence.AttributeConverter}
 * 5. 支持数据库schema自动建表，增加字段，但不会自动删除字段，自动更新字段类型
 * 6. 提供多种异步持久化容器  {@link jforgame.commons.persist.PersistContainer} {@link jforgame.commons.persist.SavingStrategy}
 */
package jforgame.orm;