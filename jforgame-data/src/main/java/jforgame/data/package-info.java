/**
 * Configuration data module.
 * Provides core capabilities including configuration table reading, container loading, common constant injection, and data validation.
 * This module depends on Spring Framework basic capabilities, but does not depend on Spring Boot.
 * When running in Spring Boot environment, automatic assembly and property binding can be done through the starter module.
 * Runtime configuration is uniformly expressed through {@link jforgame.data.ResourceOptions}, not Boot-specific configuration binding objects.
 */
package jforgame.data;
