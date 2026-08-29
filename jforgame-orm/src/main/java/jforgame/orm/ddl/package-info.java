/**
 * Light‑weight DDL schema utilities, referenced from Hibernate hbm2ddl.
 * <ul>
 *     <li>create: Drop and recreate tables at startup</li>
 *     <li>update: Auto‑create tables and add new columns, never remove existing columns</li>
 *     <li>validate: Validate metadata‑database consistency without modifying schema</li>
 * </ul>
 */
package jforgame.orm.ddl;