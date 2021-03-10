/**
 * 参考hibernate的自动建表工具
 * 综合 update 和 validate 的策略
 * 借鉴 update策略：自动建表，自动增加字段，不删除字段
 * 借鉴 validate策略：当表字段代码与数据库不一致时，给予报错提示
 */
package jforgame.orm.ddl;