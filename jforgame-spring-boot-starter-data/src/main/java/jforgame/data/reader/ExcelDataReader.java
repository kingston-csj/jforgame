package jforgame.data.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ExcelDataReader implements DataReader, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(ExcelDataReader.class.getName());
    private static final String BEGIN = "header";
    private static final String END = "end";
    private static final String EXPORT = "export";
    private final TypeDescriptor sourceType = TypeDescriptor.valueOf(String.class);

    private ApplicationContext applicationContext;

    /**
     * 是否忽略无法识别的文件字段，以javabean为准。
     * 例如假设csv文件有一个字段为name，但javabean没有同名字段，则忽略跳过；
     * 若设置为不忽略，则报会异常
     */
    private boolean ignoreUnknownFields = true;

    /**
     * 导出类型--服务端与客户端均导入
     */
    private final static String EXPORT_TYPE_BOTH = "both";

    /**
     * 导出类型--仅服务端
     */
    private final static String EXPORT_TYPE_SERVER = "server";

    /**
     * 导出类型--仅客户端
     */
    private final static String EXPORT_TYPE_CLIENT = "client";

    /**
     * 导出类型--服务端与客户端均 不 导入
     */
    private final static String EXPORT_TYPE_NONE = "none";

    @Override
    public <E> List<E> read(InputStream is, Class<E> clazz) {
        try {
            Workbook workbook = WorkbookFactory.create(is);

            boolean hasColMeta = false;
            CellHeader[] header = null;
            // 一行行的数据源
            List<CellColumn[]> records = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);
            // 获取行
            Iterator<Row> rows = sheet.rowIterator();

            // 导出类型
            String[] exportHeader = new String[0];

            // 第一行有效数据索引
            int firstDataIndex = 0;
            int index = 0;
            while (rows.hasNext()) {
                index++;
                Row row = rows.next();
                String firstCell = getCellValue(row.getCell(0));
                if (BEGIN.equalsIgnoreCase(firstCell)) {
                    header = readHeader(clazz, row);
                    hasColMeta = true;
                    continue;
                }
                if (EXPORT.equalsIgnoreCase(firstCell)) {
                    exportHeader = readExportHeader(row);
                    continue;
                }
                if (!hasColMeta) {
                    continue;
                }
                if (firstDataIndex == 0) {
                    firstDataIndex = index - 1;
                }
                records.add(readExcelRow(header, exportHeader, row));
                if (END.equalsIgnoreCase(firstCell)) {
                    // 结束符号
                    break;
                }
            }

            return readRecords(clazz, records, firstDataIndex);
        } catch (Exception e) {
            logger.error(String.format("配置表[%s]解析异常", clazz.getSimpleName()), e);
            throw new RuntimeException(e);
        }
    }

    private <E> List<E> readRecords(Class<E> clazz, List<CellColumn[]> rows, int headerIndex) throws Exception {
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
                } catch (Exception e) {
                    logger.error(String.format("配置表[%s]第%d行字段[%s]转换异常", clazz.getSimpleName(), i + headerIndex + 1, colName), e);
                    throw e;
                }
            }
            records.add(obj);
        }

        return records;
    }

    private String[] readExportHeader(Row row) {
        List<String> columns = new LinkedList<>();
        // 直接读取最后一列的序号，防止第一列空白格被跳过
        int actualColumnCount = row.getLastCellNum();
        for (int i = 1; i < actualColumnCount; i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValue(cell);
            if (!StringUtils.isEmpty(cellValue)) {
                columns.add(cellValue);
            } else {
                // 没填就是不导出
                columns.add(EXPORT_TYPE_NONE);
            }
        }
        return columns.toArray(new String[0]);
    }

    private String getExportType(String[] header, int index) {
        if (header.length <= index) {
            return EXPORT_TYPE_BOTH;
        }
        return header[index];
    }

    private CellHeader[] readHeader(Class clazz, Row row) throws NoSuchFieldException {
        List<CellHeader> columns = new ArrayList<>();
        // 直接读取最后一列的序号，防止第一列空白格被跳过
        int actualColumnCount = row.getLastCellNum();
        for (int i = 1; i < actualColumnCount; i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValue(cell);
            if (BEGIN.equalsIgnoreCase(cellValue)) {
                continue;
            }
            CellHeader header = new CellHeader();
            header.column = cellValue;
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
            columns.add(header);
        }

        return columns.toArray(new CellHeader[0]);
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        if (cell.getCellTypeEnum() != CellType.STRING) {
            cell.setCellType(CellType.STRING);
        }
        return cell.getStringCellValue();
    }

    private CellColumn[] readExcelRow(CellHeader[] headers, String[] exportHeader, Row row) {
        List<CellColumn> columns = new ArrayList<>();
        // 直接读取最后一列的序号，防止第一列空白格被跳过
        int actualColumnCount = row.getLastCellNum();
        for (int i = 1; i < actualColumnCount; i++) {
            Cell cell = row.getCell(i);
            // 表头没配，多余的列不读
            if (i - 1 >= headers.length) {
                continue;
            }
            String cellValue = getCellValue(cell);
            String exportType = getExportType(exportHeader, i - 1);
            if (EXPORT_TYPE_BOTH.equalsIgnoreCase(exportType) || EXPORT_TYPE_SERVER.equalsIgnoreCase(exportType)) {
                CellColumn column = new CellColumn();
                column.header = headers[i - 1];
                column.value = cellValue;
                columns.add(column);
            }
        }
        return columns.toArray(new CellColumn[0]);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
