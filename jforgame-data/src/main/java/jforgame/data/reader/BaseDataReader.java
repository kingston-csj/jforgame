package jforgame.data.reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置数据读取基类
 */
public abstract class BaseDataReader {

    protected static final Logger logger = LoggerFactory.getLogger(ExcelDataReader.class.getName());
    protected static final String BEGIN = "header";
    protected static final String END = "end";
    protected static final String EXPORT = "export";

    protected final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);


    /**
     * 导出类型--服务端与客户端均导入
     */
    protected final static String EXPORT_TYPE_BOTH = "both";

    /**
     * 导出类型--仅服务端
     */
    protected final static String EXPORT_TYPE_SERVER = "server";

    /**
     * 导出类型--仅客户端
     */
    protected final static String EXPORT_TYPE_CLIENT = "client";

    /**
     * 导出类型--服务端与客户端均 不 导入
     */
    protected final static String EXPORT_TYPE_NONE = "none";


    /**
     * 是否忽略无法识别的文件字段，以javabean为准。
     * 例如假设csv文件有一个字段为name，但javabean没有同名字段，则忽略跳过；
     * 若设置为不忽略，则报会异常
     */
    protected boolean ignoreUnknownFields = true;

    /**
     * 数据转换，请使用jforgame-data提供的名为“dataConversionService”的组件
     */
    protected ConversionService dataConversionService;

    public BaseDataReader(ConversionService dataConversionService) {
        this.dataConversionService = dataConversionService;
    }

    /**
     * 查找类及其父类中声明的字段。
     *
     * @param clazz     要查找的类
     * @param fieldName 要查找的字段名
     * @return 找到的字段
     * @throws NoSuchFieldException 如果未找到字段
     */
    protected Field findFieldInClassHierarchy(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        if (clazz == null || clazz.equals(Object.class)) {
            throw new NoSuchFieldException(fieldName);
        }
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return findFieldInClassHierarchy(clazz.getSuperclass(), fieldName);
        }
    }

    /**
     * 获取字段导出类型
     *
     * @param header 表头
     * @param index  列索引
     * @return 导出类型
     */
    protected String getExportType(String[] header, int index) {
        // 没有header列或字段缺失
        if (header.length == 0 || header.length <= index) {
            return EXPORT_TYPE_BOTH;
        }
        return header[index];
    }

    protected <E> List<E> readRecords(Class<E> clazz, String[] exportHeader, List<CellColumn[]> rows, int headerIndex) throws Exception {
        List<E> records = new ArrayList<>(rows.size());
        for (int i = 0; i < rows.size(); i++) {
            CellColumn[] record = rows.get(i);
            E obj = clazz.newInstance();
            // 每一行的字段索引
            for (int j = 0; j < record.length; j++) {
                CellColumn column = record[j];
                if (column == null) {
                    continue;
                }
                String colName = column.header.column;
                if (StringUtils.isEmpty(colName)) {
                    continue;
                }
                String exportType = getExportType(exportHeader, j);
                if (EXPORT_TYPE_BOTH.equalsIgnoreCase(exportType) || EXPORT_TYPE_SERVER.equalsIgnoreCase(exportType)) {
                    try {
                        Field field = findFieldInClassHierarchy(clazz, colName);
                        field.setAccessible(true);
                        Object fieldVal = dataConversionService.convert(column.value, sourceType, new TypeDescriptor(field));
                        field.set(obj, fieldVal);
                    } catch (NoSuchFieldException e) {
                        if (!ignoreUnknownFields) {
                            throw e;
                        }
                    } catch (Exception e) {
                        logger.error(String.format("配置表[%s]第%d行字段[%s]转换异常", clazz.getSimpleName(), i + headerIndex + 1, colName), e);
                        throw e;
                    }
                }
            }
            records.add(obj);
        }

        return records;
    }

    /**
     * 设置是否忽略无法识别的文件字段
     */
    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

}
