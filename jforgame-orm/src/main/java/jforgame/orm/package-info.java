/**
 * <p>
 * Light‑weight annotation‑driven ORM engine, implements a subset of Jakarta Persistence(JPA) annotations,
 * not a full JPA‑compliant container.
 * <ul>
 * <li>Supported annotations: {@link javax.persistence.Entity}, {@link javax.persistence.Id},
 *     {@link javax.persistence.Column}, {@link javax.persistence.Table}, {@link javax.persistence.Index},
 *     {@link javax.persistence.Convert}
 * <li>Opt‑in persistence rule: only fields annotated with {@code @Id} or {@code @Column} are mapped
 * <li>Composite index and unique index via {@code @Table#indexes()}
 * <li>SPI‑style {@link javax.persistence.AttributeConverter}, with context‑aware extension for generic JSON mapping
 * </ul>
 * <p>
 * Game‑server oriented persistence model: single‑table operations, cache‑first, periodic batch async persistence,
 * eventual‑consistency guaranteed by persist containers {@link jforgame.commons.persist.PersistContainer}
 * and {@link jforgame.commons.persist.SavingStrategy}.
 * <ul>
 * <li>DbUtils‑like lightweight API</li>
 * <li>Multi‑data‑source support</li>
 * <li>Full‑field update and incremental partial update</li>
 * <li>Safe auto DDL: create tables and add new columns, never drop or modify existing columns</li>
 * </ul>
 */
package jforgame.orm;