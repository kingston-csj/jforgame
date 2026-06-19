package jforgame.data.common;

/**
 * After parsing {@link CommonData} configuration, this interface will be called back to refresh secondary caches related to common constants
 * Only implement this interface when the Service itself performs secondary parsing on values annotated with {@link CommonConfig}, 
 * such as converting strings to maps and storing them independently
 */
public interface CommonValueReloadListener {

    /**
     * Listen for configuration changes
     */
    void afterReload();
}
