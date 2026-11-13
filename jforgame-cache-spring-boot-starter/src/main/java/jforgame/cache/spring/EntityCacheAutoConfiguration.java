package jforgame.cache.spring;

import jforgame.cache.CacheStore;
import jforgame.cache.DatabaseAccessor;
import jforgame.cache.EntityCacheService;
import jforgame.cache.EntityCacheServiceFactory;
import jforgame.cache.EntityCacheServiceFactoryImpl;
import jforgame.cache.LocalCacheStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
 * <p>A {@link DatabaseAccessor} bean must be provided by the application,
 * for example by implementing {@code DatabaseAccessor} on top of jforgame-orm or MyBatis.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(EntityCacheService.class)
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
}
