package jforgame.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jforgame.data", ignoreInvalidFields = true)
public class ResourceProperties {

    /**
     * 资源根目录
     */
    private String location = "csv/";

    /**
     * 文件后缀(默认)
     */
    private String suffix = ".csv";

    /**
     * 配置实体{@link jforgame.data.annotation.DataTable}扫描路径
     */
    private String tableScanPath;


    /**
     * 配置容器{@link Container}子类的扫描路径
     */
    private String containerScanPath;

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
}
