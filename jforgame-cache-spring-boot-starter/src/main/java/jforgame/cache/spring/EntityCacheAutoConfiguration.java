package jforgame.cache.spring;

import jforgame.cache.CacheStore;
import jforgame.cache.DatabaseAccessor;
import jforgame.cache.EntityCacheService;
import jforgame.cache.EntityCacheServiceFactory;
import jforgame.cache.EntityCacheServiceFactoryImpl;
import jforgame.cache.LocalCacheStore;
import org.hibernate.Session;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;

/**
 * Autoconfiguration for the entity cache framework.
 *
 * <p>Cache store selection:
 * <ul>
 *   <li>a user-defined {@link CacheStore} bean always wins;</li>
 *   <li>when Spring Cache is on the classpath, {@link SpringCacheStore} is used
 *       so caches share the Spring {@link CacheManager};</li>
 *   <li>otherwise {@link LocalCacheStore} is used.</li>
 * </ul>
 *
 * <p>When JPA/Hibernate is on the classpath and an {@link EntityManagerFactory} is
 * available, {@link JpaDatabaseAccessor} is registered as the default
 * {@link DatabaseAccessor}. Applications using another persistence framework must
 * provide their own {@link DatabaseAccessor} bean.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EntityCacheService.class)
@AutoConfigureAfter(name = "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration")
@EnableConfigurationProperties(EntityCacheProperties.class)
public class EntityCacheAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EntityCacheServiceFactory entityCacheServiceFactory(DatabaseAccessor databaseAccessor, CacheStore cacheStore) {
        return new EntityCacheServiceFactoryImpl(databaseAccessor, cacheStore);
    }

    @Bean
    public static EntityCacheServiceInjectProcessor entityCacheServiceInjectProcessor(
            ObjectProvider<EntityCacheServiceFactory> factoryProvider, EntityCacheProperties properties) {
        return new EntityCacheServiceInjectProcessor(factoryProvider, properties);
    }

    /**
     * Uses Spring Cache when it is available and the application has not defined its own store.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(CacheManager.class)
    static class SpringCacheStoreConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public CacheStore springCacheStore(CacheManager cacheManager) {
            return new SpringCacheStore(cacheManager);
        }
    }

    /**
     * Falls back to the in-memory store when Spring Cache is not on the classpath.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnMissingClass("org.springframework.cache.CacheManager")
    static class LocalCacheStoreConfiguration {

        @Bean
        @ConditionalOnMissingBean
        public CacheStore localCacheStore() {
            return new LocalCacheStore();
        }
    }

    /**
     * Default database accessor backed by JPA/Hibernate.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({EntityManagerFactory.class, Session.class})
    static class JpaDatabaseAccessorConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnSingleCandidate(EntityManagerFactory.class)
        public DatabaseAccessor jpaDatabaseAccessor(EntityManagerFactory entityManagerFactory) {
            return new JpaDatabaseAccessor(entityManagerFactory);
        }
    }
}
