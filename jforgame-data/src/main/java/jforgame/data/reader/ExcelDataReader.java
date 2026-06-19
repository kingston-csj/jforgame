package jforgame.data.reader;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Configuration table reader based on Excel files
 */
public class ExcelDataReader extends BaseDataReader implements DataReader {

    public ExcelDataReader(ConversionService dataConversionService) {
        super(dataConversionService);
    }

    @Override
    public <E> List<E> read(InputStream is, Class<E> clazz) {
        try {
            Workbook workbook = WorkbookFactory.create(is);
            boolean hasColMeta = false;
            CellHeader[] header = null;
            // Row by row data source
            List<CellColumn[]> records = new ArrayList<>();

            Sheet sheet = workbook.getSheetAt(0);
            // Get rows
            Iterator<Row> rows = sheet.rowIterator();

            // Export type
            String[] exportHeader = new String[0];

            // First valid data row index
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
                    // End marker
                    break;
                }
            }

            return readRecords(clazz, exportHeader, records, firstDataIndex);
        } catch (Exception e) {
            logger.error(String.format("Configuration table [%s] parsing exception", clazz.getSimpleName()), e);
            throw new RuntimeException(e);
        }
    }


    private String[] readExportHeader(Row row) {
        List<String> columns = new LinkedList<>();
        // Read the last column index directly to prevent the first blank cell from being skipped
        int actualColumnCount = row.getLastCellNum();
        for (int i = 1; i < actualColumnCount; i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValue(cell);
            if (!StringUtils.isEmpty(cellValue)) {
                columns.add(cellValue);
            } else {
                // Not filled means no export
                columns.add(EXPORT_TYPE_NONE);
            }
        }
        return columns.toArray(new String[0]);
    }

    private CellHeader[] readHeader(Class clazz, Row row) throws NoSuchFieldException {
        List<CellHeader> columns = new ArrayList<>();
        // Read the last column index directly to prevent the first blank cell from being skipped
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

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        // Convert to string uniformly
        // Converter will automatically convert to the required business type
        if (cell.getCellType() != CellType.STRING) {
            cell.setCellType(CellType.STRING);
        }
        return cell.getStringCellValue();
    }

    private CellColumn[] readExcelRow(CellHeader[] headers, String[] exportHeader, Row row) {
        List<CellColumn> columns = new ArrayList<>();
        // Read the last column index directly to prevent the first blank cell from being skipped
        int actualColumnCount = row.getLastCellNum();
        for (int i = 1; i < actualColumnCount; i++) {
            Cell cell = row.getCell(i);
            // Skip extra columns not configured in header
            if (i - 1 >= headers.length) {
                continue;
            }
            String cellValue = getCellValue(cell);
            // header doesn't include column 1, but record includes column 1, so i-1
            String exportType = getExportType(exportHeader, i - 1);
            if (EXPORT_TYPE_BOTH.equalsIgnoreCase(exportType) || EXPORT_TYPE_SERVER.equalsIgnoreCase(exportType)) {
                CellColumn column = new CellColumn();
                column.header = headers[i - 1];
                column.value = cellValue;
                columns.add(column);
            } else {
                // Empty column placeholder
                columns.add(null);
            }
        }
        return columns.toArray(new CellColumn[0]);
    }


}
