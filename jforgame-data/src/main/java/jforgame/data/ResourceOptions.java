package jforgame.data;

/**
 * 数据资源加载配置。
 * 这是一个普通的配置对象，不依赖 Spring Boot 的配置绑定能力。
 */
public class ResourceOptions {

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

    /**
     * 通用常量表表名，默认为common.csv/common.xlsx
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
}
