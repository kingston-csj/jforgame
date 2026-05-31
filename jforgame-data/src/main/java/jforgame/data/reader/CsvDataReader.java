package jforgame.data.reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 基于csv文件的配置表读取器
 */
public class CsvDataReader extends BaseDataReader implements DataReader {

    public CsvDataReader(ConversionService dataConversionService) {
        super(dataConversionService);
    }


    @Override
    public <E> List<E> read(InputStream is, Class<E> clazz) {
        Reader in = new InputStreamReader(is);
        try {
            Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
            boolean hasColMeta = false;
            CellHeader[] header = null;
            // 一行行的数据源
            List<CellColumn[]> rows = new ArrayList<>();
            // 导出类型
            String[] exportHeader = new String[0];
            // 第一行有效数据索引
            int firstDataIndex = 0;
            int index = 0;
            for (CSVRecord record : records) {
                index++;
                // BEGIN前面的数据无效
                if (BEGIN.equalsIgnoreCase(record.get(0))) {
                    header = readHeader(clazz, record);
                    hasColMeta = true;
                    continue;
                }
                if (EXPORT.equalsIgnoreCase(record.get(0))) {
                    exportHeader = readExportHeader(record);
                    continue;
                }
                if (!hasColMeta) {
                    continue;
                }
                if (firstDataIndex == 0) {
                    firstDataIndex = index - 1;
                }
                rows.add(readCsvRow(header, exportHeader, record));
                if (END.equalsIgnoreCase(record.get(0))) {
                    // 结束符号
                    break;
                }
            }
            return readRecords(clazz, exportHeader, rows, firstDataIndex);
        } catch (Exception e) {
            logger.error("", e);
            throw new RuntimeException(e);
        }
    }

    private String[] readExportHeader(CSVRecord record) {
        List<String> columns = new LinkedList<>();
        for (int i = 1; i < record.size(); i++) {
            String cellValue = record.get(i);
            if (!StringUtils.isEmpty(cellValue)) {
                columns.add(cellValue);
            } else {
                // 没填就是不导出
                columns.add(EXPORT_TYPE_NONE);
            }
        }
        return columns.toArray(new String[0]);
    }

    private CellHeader[] readHeader(Class clazz, CSVRecord record) throws NoSuchFieldException {
        List<CellHeader> columns = new ArrayList<>();
        for (int i = 1; i < record.size(); i++) {
            CellHeader header = new CellHeader();
            header.column = record.get(i);
            if (!StringUtils.isEmpty(header.column)) {
                try {
                    header.field = findFieldInClassHierarchy(clazz, header.column);
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


    private CellColumn[] readCsvRow(CellHeader[] headers, String[] exportHeader, CSVRecord record) {
        List<CellColumn> columns = new ArrayList<>();
        for (int i = 1; i < record.size(); i++) {
            // header不包含第1列，但record包含第1列，所以i-1
            String exportType = getExportType(exportHeader, i - 1);
            if (EXPORT_TYPE_BOTH.equalsIgnoreCase(exportType) || EXPORT_TYPE_SERVER.equalsIgnoreCase(exportType)) {
                CellColumn column = new CellColumn();
                column.header = headers[i - 1];
                column.value = record.get(i);
                columns.add(column);
            } else {
                // 空列占位
                columns.add(null);
            }
        }
        return columns.toArray(new CellColumn[0]);
    }

}


