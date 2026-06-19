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
 * Base class for configuration data reading
 */
public abstract class BaseDataReader {

    protected static final Logger logger = LoggerFactory.getLogger(ExcelDataReader.class.getName());
    protected static final String BEGIN = "header";
    protected static final String END = "end";
    protected static final String EXPORT = "export";

    protected final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);


    /**
     * Export type -- import to both server and client
     */
    protected final static String EXPORT_TYPE_BOTH = "both";

    /**
     * Export type -- server only
     */
    protected final static String EXPORT_TYPE_SERVER = "server";

    /**
     * Export type -- client only
     */
    protected final static String EXPORT_TYPE_CLIENT = "client";

    /**
     * Export type -- import to neither server nor client
     */
    protected final static String EXPORT_TYPE_NONE = "none";


    /**
     * Whether to ignore unrecognized file fields, using javabean as the source of truth.
     * For example, if a csv file has a field named 'name' but the javabean doesn't have a field with the same name, it will be skipped;
     * If set to not ignore, an exception will be thrown.
     */
    protected boolean ignoreUnknownFields = true;

    /**
     * Data conversion service, use the component named "dataConversionService" provided by jforgame-data
     */
    protected ConversionService dataConversionService;

    public BaseDataReader(ConversionService dataConversionService) {
        this.dataConversionService = dataConversionService;
    }

    /**
     * Finds a field declared in a class and its parent classes.
     *
     * @param clazz     the class to search in
     * @param fieldName the field name to search for
     * @return the found field
     * @throws NoSuchFieldException if field not found
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
     * Gets field export type
     *
     * @param header the header row
     * @param index  the column index
     * @return the export type
     */
    protected String getExportType(String[] header, int index) {
        // No header column or field missing
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
            // Field index for each row
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
                        logger.error(String.format("Configuration table [%s] row %d field [%s] conversion exception", clazz.getSimpleName(), i + headerIndex + 1, colName), e);
                        throw e;
                    }
                }
            }
            records.add(obj);
        }

        return records;
    }

    /**
     * Sets whether to ignore unrecognized file fields
     */
    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }

}
