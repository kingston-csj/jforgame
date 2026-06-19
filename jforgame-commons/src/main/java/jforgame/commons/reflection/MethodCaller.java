package jforgame.commons.reflection;

/**
 * Method invocation interface
 * This interface provides a non-direct method invocation approach,
 * such as reflection, method handle, asm, etc.
 * @since 2.4.0
 */
@FunctionalInterface
public interface MethodCaller {

    /**
     * Invokes a method
     *
     * @param target the target object
     * @param params the parameters
     * @return the method return value
     * @throws Exception exception
     */
    Object invoke(Object target, Object[] params) throws Exception;

}