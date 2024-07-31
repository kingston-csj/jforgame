package jforgame.data.reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CsvDataReader implements DataReader, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CsvDataReader.class.getName());
    private static final String BEGIN = "header";
    private static final String END = "end";
    private final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

    private ApplicationContext applicationContext;

    /**
     * 是否忽略无法识别的文件字段，以javabean为准。
     * 例如假设csv文件有一个字段为name，但javabean没有同名字段，则忽略跳过；
     * 若设置为不忽略，则报会异常
     */
    private boolean ignoreUnknownFields = true;

    @Override
    public <E> List<E> read(InputStream is, Class<E> clazz) {
        Reader in = new InputStreamReader(is);
        try {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            boolean hasColMeta = false;
            CellHeader[] header = null;
            // 一行行的数据源
            List<CellColumn[]> rows = new ArrayList<>();
            for (CSVRecord record : records) {
                // BEGIN前面的数据无效
                if (BEGIN.equalsIgnoreCase(record.get(0))) {
                    header = readHeader(clazz, record);
                    hasColMeta = true;
                    continue;
                }
                if (!hasColMeta) {
                    continue;
                }
                rows.add(readCsvRow(header, record));
                if (END.equalsIgnoreCase(record.get(0))) {
                    // 结束符号
                    break;
                }
            }
            return readRecords(clazz, rows);
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    private <E> List<E> readRecords(Class<E> clazz, List<CellColumn[]> rows) throws Exception {
        List<E> records = new ArrayList<>(rows.size());
        ConversionService conversionService = applicationContext.getBean("dataConversionService", ConversionService.class);
        for (int i = 0; i < rows.size(); i++) {
            CellColumn[] record = rows.get(i);
            E obj = clazz.newInstance();

            for (CellColumn column : record) {
                String colName = column.header.column;
                if (StringUtils.isEmpty(colName)) {
                    continue;
                }
                try {
                    Field field = clazz.getDeclaredField(colName);
                    field.setAccessible(true);
                    Object fieldVal = conversionService.convert(column.value, sourceType, new TypeDescriptor(field));
                    field.set(obj, fieldVal);
                } catch (NoSuchFieldException e) {
                    if (!ignoreUnknownFields) {
                        throw e;
                    }
                }
            }

            records.add(obj);
        }
        return records;
    }

    private CellHeader[] readHeader(Class clazz, CSVRecord record) throws NoSuchFieldException {
        CellHeader[] columns = new CellHeader[record.size() - 1];
        for (int i = 0; i < columns.length; i++) {
            CellHeader header = new CellHeader();
            header.column = record.get(i + 1);
            if (!StringUtils.isEmpty(header.column)) {
                try {
                    header.field = clazz.getDeclaredField(header.column);
                    header.field.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    if (!ignoreUnknownFields) {
                        throw e;
                    }
                }
            }

            columns[i] = header;
        }
        return columns;
    }

    private CellColumn[] readCsvRow(CellHeader[] headers, CSVRecord record) {
        CellColumn[] columns = new CellColumn[record.size() - 1];
        for (int i = 0; i < columns.length; i++) {
            CellColumn column = new CellColumn();
            column.header = headers[i];
            column.value = record.get(i + 1);
            columns[i] = column;

        }
        return columns;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setIgnoreUnknownFields(boolean ignoreUnknownFields) {
        this.ignoreUnknownFields = ignoreUnknownFields;
    }
}


