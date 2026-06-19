/**
 * Reference Hibernate's automatic table creation tool.
 * Combines update and validate strategies.
 * Draws from update strategy: auto create tables, auto add fields, do not delete fields.
 * Draws from validate strategy: give error when table field code is inconsistent with database.
 */
package jforgame.orm.ddl;