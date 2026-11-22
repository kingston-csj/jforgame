package jforgame.cache.spring;

import jforgame.cache.DatabaseAccessor;
import jforgame.commons.persist.Entity;
import org.hibernate.Session;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

/**
 * Default {@link DatabaseAccessor} implementation backed by JPA / Hibernate.
 *
 * <p>Entity classes must be JPA entities that implement {@link Entity}. Each operation
 * uses an independent {@link EntityManager} and transaction, which matches the
 * framework's cache semantics: the cache layer does not participate in application
 * transactions, so JTA environments are not supported.
 *
 * <p>Lazy associations are not available after an entity is loaded, because the
 * {@link EntityManager} is closed immediately. Entities used with this accessor should
 * be fully initialized (eager associations or fetched joins) before being cached.
 */
public final class JpaDatabaseAccessor implements DatabaseAccessor {

    private final EntityManagerFactory entityManagerFactory;

    /**
     * Constructor.
     *
     * @param entityManagerFactory the JPA entity manager factory
     */
    public JpaDatabaseAccessor(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public <E> E load(Class<E> entityClass, Object id) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            return entityManager.find(entityClass, id);
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void saveOrUpdate(Entity<?> entity) {
        inTransaction(entityManager -> entityManager.unwrap(Session.class).saveOrUpdate(entity));
    }

    @Override
    public void remove(Entity<?> entity) {
        inTransaction(entityManager -> {
            Object managed = entityManager.contains(entity) ? entity : entityManager.merge(entity);
            entityManager.remove(managed);
        });
    }

    private void inTransaction(EntityManagerCallback callback) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            callback.doInEntityManager(entityManager);
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            entityManager.close();
        }
    }

    @FunctionalInterface
    private interface EntityManagerCallback {

        void doInEntityManager(EntityManager entityManager);

    }
}
