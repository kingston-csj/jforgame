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
 * 基于excel文件的配置表读取器
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

            return readRecords(clazz, exportHeader, records, firstDataIndex);
        } catch (Exception e) {
            logger.error(String.format("配置表[%s]解析异常", clazz.getSimpleName()), e);
            throw new RuntimeException(e);
        }
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
        // 统一转换为字符串
        // 转换器会自动转换为业务需要的类型
        if (cell.getCellType() != CellType.STRING) {
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
            // header不包含第1列，但record包含第1列，所以i-1
            String exportType = getExportType(exportHeader, i - 1);
            if (EXPORT_TYPE_BOTH.equalsIgnoreCase(exportType) || EXPORT_TYPE_SERVER.equalsIgnoreCase(exportType)) {
                CellColumn column = new CellColumn();
                column.header = headers[i - 1];
                column.value = cellValue;
                columns.add(column);
            } else {
                // 空列占位
                columns.add(null);
            }
        }
        return columns.toArray(new CellColumn[0]);
    }


}
