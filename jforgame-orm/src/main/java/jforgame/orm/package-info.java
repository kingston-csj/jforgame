/**
 * 自定义ORM框架，有以下特点
 * 1. 允许多数据源
 * 2. 支持全字段更新与增量字段更新
 * 3. 支持ORM属性数据转换 {@link jforgame.orm.converter.Convert} {@link jforgame.orm.converter.AttributeConverter}
 * 4. 支持数据库schema自动建表，增加字段
 *
 * TODO 参考mybatics防止sql注入
 */
package jforgame.orm;