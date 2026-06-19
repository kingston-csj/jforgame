package jforgame.data.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jforgame.commons.util.FileUtil;
import jforgame.commons.util.StringUtil;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Uses JSON file as data source
 * The key of each record in the JSON file is similar to the Excel header
 */
public class JsonDataReader extends BaseDataReader implements DataReader {

    private ObjectMapper objectMapper = new ObjectMapper();

    public JsonDataReader(ConversionService dataConversionService) {
        super(dataConversionService);

    }

    @Override
    public <E> List<E> read(InputStream is, Class<E> clazz) {
        List<E> records = new LinkedList<>();
        try {
            String json = FileUtil.readFullText(is).trim();
            // Remove leading UTF-8 BOM character (0xFEFF). Some editors add this hidden character when saving, so it needs to be filtered first.
            if (StringUtil.isNotEmpty(json) && json.startsWith("\uFEFF")) {
                json = json.substring(1); // Remove the first hidden BOM character
            }
            // Treat JSON file as a data list, each row represents one configuration record. Key is string, value is Object
            List<Map<String, Object>> dataList = objectMapper.readValue(json,
                    new TypeReference<List<Map<String, Object>>>() {}
            );
            for (Map<String, Object> data : dataList) {
                E record;
                try {
                    record = clazz.getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                // If entry.getValue() is not a primitive type or string, it will be further converted through ConversionService
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    if (entry.getValue() == null || "null".equals(entry.getValue())) {
                        continue;
                    }
                    String colName = entry.getKey();
                    try {
                        Field field = findFieldInClassHierarchy(clazz, colName);
                        field.setAccessible(true);
                        Object fieldVal = dataConversionService.convert(entry.getValue(), TypeDescriptor.valueOf(entry.getValue().getClass()), new TypeDescriptor(field));
                        field.set(record, fieldVal);
                    } catch (NoSuchFieldException ignored) {
                        // JSON files don't have headers, so we can't determine which fields are client-only.
                        // We can only determine based on whether the bean has a field with the same name.
                        // If the field doesn't exist, it's considered a client field and is ignored.
                    } catch (Exception e) {
                        logger.error(String.format("Configuration table [%s] field [%s] conversion exception", clazz.getSimpleName(), colName), e);
                        throw e;
                    }
                }
                records.add(record);
            }
            return records;
        } catch (Exception e) {
            logger.error(String.format("Configuration table [%s] parsing exception", clazz.getSimpleName()), e);
            throw new RuntimeException(e);
        }
    }

}
