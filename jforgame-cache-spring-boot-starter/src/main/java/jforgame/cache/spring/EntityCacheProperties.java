package jforgame.cache.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for entity cache auto-injection.
 * Controls which packages are scanned for {@link EntityCacheAutowired} annotations.
 */
@ConfigurationProperties(prefix = "jforgame.cache")
public class EntityCacheProperties {

    /**
     * Base packages to scan for {@link EntityCacheAutowired} annotations.
     * If empty, scans all packages.
     */
    private List<String> basePackages = new ArrayList<>();

    /**
     * Gets the base packages to scan.
     *
     * @return the list of base packages
     */
    public List<String> getBasePackages() {
        return basePackages;
    }

    /**
     * Sets the base packages to scan.
     *
     * @param basePackages the list of base packages
     */
    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages != null ? basePackages : new ArrayList<>();
    }

    /**
     * Checks if a class name matches any of the configured base packages.
     * If basePackages is empty, returns true (scan all).
     *
     * @param className the fully qualified class name
     * @return true if the class should be scanned
     */
    public boolean shouldScan(String className) {
        if (basePackages == null || basePackages.isEmpty()) {
            return true;
        }
        for (String pkg : basePackages) {
            if (className.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }
}
