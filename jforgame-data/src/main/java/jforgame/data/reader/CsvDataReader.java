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
 * Configuration table reader based on CSV files
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
            // Row by row data source
            List<CellColumn[]> rows = new ArrayList<>();
            // Export type
            String[] exportHeader = new String[0];
            // First valid data row index
            int firstDataIndex = 0;
            int index = 0;
            for (CSVRecord record : records) {
                index++;
                // Data before BEGIN is invalid
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
                    // End marker
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
                // Not filled means no export
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
            // header doesn't include column 1, but record includes column 1, so i-1
            String exportType = getExportType(exportHeader, i - 1);
            if (EXPORT_TYPE_BOTH.equalsIgnoreCase(exportType) || EXPORT_TYPE_SERVER.equalsIgnoreCase(exportType)) {
                CellColumn column = new CellColumn();
                column.header = headers[i - 1];
                column.value = record.get(i);
                columns.add(column);
            } else {
                    // Empty column placeholder
                    columns.add(null);
                }
        }
        return columns.toArray(new CellColumn[0]);
    }

}


