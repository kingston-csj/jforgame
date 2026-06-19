package jforgame.data.autoconfigure;

import jforgame.data.Container;
import jforgame.data.ResourceOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jforgame.data", ignoreInvalidFields = true)
public class ResourceProperties {

    /**
     * Resource root directory
     */
    private String location = "excel/";

    /**
     * File suffix (default)
     */
    private String suffix = ".xlsx";

    /**
     * Configuration entity {@link jforgame.data.annotation.DataTable} scan path
     */
    private String tableScanPath;


    /**
     * Configuration container {@link Container} subclass scan path
     */
    private String containerScanPath;

    /**
     * Common constant table name, default is common.csv/common.xlsx
     */
    private String commonTableName = "common";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getTableScanPath() {
        return tableScanPath;
    }

    public void setTableScanPath(String tableScanPath) {
        this.tableScanPath = tableScanPath;
    }

    public String getContainerScanPath() {
        return containerScanPath;
    }

    public void setContainerScanPath(String containerScanPath) {
        this.containerScanPath = containerScanPath;
    }

    public String getCommonTableName() {
        return commonTableName;
    }

    public void setCommonTableName(String commonTableName) {
        this.commonTableName = commonTableName;
    }

    public ResourceOptions toResourceOptions() {
        ResourceOptions options = new ResourceOptions();
        options.setLocation(location);
        options.setSuffix(suffix);
        options.setTableScanPath(tableScanPath);
        options.setContainerScanPath(containerScanPath);
        options.setCommonTableName(commonTableName);
        return options;
    }
}
