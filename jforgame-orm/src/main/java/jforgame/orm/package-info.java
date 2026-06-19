/**
 * Characteristics of ORM in game domain:
 * Single table operations: Each operation involves only one table, no transaction needed for consistency guarantee.
 * Cache first: Data is first written to memory cache to ensure response speed.
 * Async persistence: Periodically batch write to database to improve performance.
 * Eventual consistency: Ensure data eventual consistency through other mechanisms.
 * Custom ORM framework has the following characteristics:
 * 1. Provides API similar to Apache DbUtils, super lightweight, perfectly adapted to ORM characteristics of game servers.
 * 2. Supports multiple data sources.
 * 3. Supports full field update and incremental field update.
 * 4. Supports ORM attribute data conversion {@link javax.persistence.AttributeConverter}.
 * 5. Supports automatic database schema table creation and field addition, but will not automatically delete fields or change field types.
 * 6. Provides multiple async persistence containers {@link jforgame.commons.persist.PersistContainer} {@link jforgame.commons.persist.SavingStrategy}.
 */
package jforgame.orm;