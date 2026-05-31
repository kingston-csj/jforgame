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
 * 使用json文件作为数据源
 * json文件每条记录的的key值类似excel的表头
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
            // 去除开头的UTF-8 BOM字符（0xFEFF） 部分编辑器保存的时候会有该隐藏字符，需要先过滤
            if (StringUtil.isNotEmpty(json) && json.startsWith("\uFEFF")) {
                json = json.substring(1); // 截取掉第一个隐藏的BOM字符
            }
            // 把json文件当作数据列表，每一行记录代表一行配置, key统一为string, value为Object
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
                // 如果entry.getValue()的值不是基本类型或string，则会通过ConversionService作进一步转换
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
                        // json没有表头，无法判断哪些字段是纯客户端字段，只能根据bean是否有同名field来判断
                        // 如果没有该filed,则判定为客户端字段，忽略即可
                    } catch (Exception e) {
                        logger.error(String.format("配置表[%s] 字段[%s]转换异常", clazz.getSimpleName(), colName), e);
                        throw e;
                    }
                }
                records.add(record);
            }
            return records;
        } catch (Exception e) {
            logger.error(String.format("配置表[%s]解析异常", clazz.getSimpleName()), e);
            throw new RuntimeException(e);
        }
    }

}
