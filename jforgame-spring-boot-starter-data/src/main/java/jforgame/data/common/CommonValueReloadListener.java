package jforgame.data.common;

/**
 * 解析{@link CommonData}配置后，会对该接口进行回调，用于刷新通用常量相关的二级缓存
 * 但且仅当Service本身对{@link CommonConfig}注解的值进行二次解析，例如将string转成map后独立存储，需要实现该接口
 */
public interface CommonValueReloadListener {

    /**
     * 监听配置发生变化
     */
    void afterReload();
}
