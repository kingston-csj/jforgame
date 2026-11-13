package jforgame.cache.spring;

import jforgame.cache.BaseEntityCacheService;
import jforgame.cache.EntityCacheServiceFactory;
import jforgame.commons.persist.Entity;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

/**
 * Bean post processor that injects entity cache services into fields annotated with
 * {@link EntityCacheAutowired}. Service instances are created by {@link EntityCacheServiceFactory}.
 *
 * <p>Optimized with two-level filtering:
 * <ol>
 *   <li>Package filtering via {@link EntityCacheProperties} - only scans configured packages</li>
 *   <li>Type pre-check - quickly filters beans without {@link BaseEntityCacheService} fields before reflection</li>
 * </ol>
 */
public class EntityCacheServiceInjectProcessor implements BeanPostProcessor, Ordered {

    private final ObjectProvider<EntityCacheServiceFactory> factoryProvider;

    private final EntityCacheProperties properties;

    public EntityCacheServiceInjectProcessor(ObjectProvider<EntityCacheServiceFactory> factoryProvider,
                                             EntityCacheProperties properties) {
        this.factoryProvider = factoryProvider;
        this.properties = properties;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = bean.getClass();

        //  Package filtering - skip beans from non-configured packages
        if (!properties.shouldScan(targetClass.getName())) {
            return bean;
        }

        //   Type pre-check - skip beans without BaseEntityCacheService fields
        if (!hasEntityCacheField(targetClass)) {
            return bean;
        }

        //  Deep reflection scan only for matching beans
        ReflectionUtils.doWithFields(targetClass, field -> {
            if (field.isAnnotationPresent(EntityCacheAutowired.class) &&
                    BaseEntityCacheService.class.isAssignableFrom(field.getType())) {

                BaseEntityCacheService<?> service = resolveService(extractEntityClass(field));

                ReflectionUtils.makeAccessible(field);
                field.set(bean, service);
            }
        });

        return bean;
    }

    /**
     * Fast pre-check to determine if a class has any field of type {@link BaseEntityCacheService}.
     * This avoids expensive ReflectionUtils traversal for beans that don't need it.
     *
     * @param clazz the class to check
     * @return true if the class has BaseEntityCacheService fields
     */
    private boolean hasEntityCacheField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .anyMatch(field -> BaseEntityCacheService.class.isAssignableFrom(field.getType()));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private BaseEntityCacheService<?> resolveService(Class<? extends Entity> entityClass) {
        return factoryProvider.getObject().create((Class) entityClass);
    }

    /**
     * Extracts the entity class from the field's generic type.
     *
     * @param field the field annotated with @EntityCacheAutowired
     * @return the entity class
     */
    private Class<? extends Entity> extractEntityClass(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] actualTypeArguments = paramType.getActualTypeArguments();

            if (actualTypeArguments.length > 0) {
                Type entityType = actualTypeArguments[0];
                if (entityType instanceof Class) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Entity> entityClass = (Class<? extends Entity>) entityType;
                    return entityClass;
                }
            }
        }

        throw new BeanCreationException("Cannot extract entity class from field " + field.getName());
    }
}
