package jforgame.commons.reflection;

/**
 * 方法调用接口
 * 该接口提供一个非直接调用方法的方式，
 * 例如：反射，方法句柄，asm等技术
 * @since 2.4.0
 */
@FunctionalInterface
public interface MethodCaller {

    /**
     * 调用方法
     *
     * @param target 目标对象
     * @param params 参数
     * @return 方法返回值
     * @throws Exception 异常
     */
    Object invoke(Object target, Object[] params) throws Exception;

}