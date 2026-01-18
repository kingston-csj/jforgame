package jforgame.demo.tools.protocol.diy;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import jforgame.commons.util.FileUtil;
import jforgame.socket.share.message.Message;
import jforgame.socket.share.message.Response;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CSharpProtocolGenerator extends ProtocolDocGenerator {

    public void export() throws Exception {
        this.collect();
        for (ProtocolFileDoc doc : docs) {
            export0(doc);
        }
    }

    private void export0(ProtocolFileDoc protocolFileDoc) throws Exception {
        for (ProtocolFieldDoc fieldDoc : protocolFileDoc.getFields()) {
            fieldDoc.setType(fieldOfTypeScript(fieldDoc.getMeta()));
        }

        // 创建FreeMarker配置对象
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
        cfg.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);

        // 获取模板文件
        Template template = cfg.getTemplate("csharpprotocol.ftl");
        String newDirectory = "game_protocol/csharp/";
        // 上两层包名作为目录
        String packagePath = protocolFileDoc.getClazz().getPackage().getName();
        String moduleName = packagePath.substring(packagePath.indexOf("game.") + 5, packagePath.lastIndexOf("."));
        moduleName = moduleName.substring(moduleName.lastIndexOf(".") + 1);
        newDirectory = newDirectory + moduleName;
        FileUtil.checkAndCreateDirectory(newDirectory);
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("className", protocolFileDoc.getName());
        dataModel.put("classComment", protocolFileDoc.getDesc().trim());
        dataModel.put("fieldList", protocolFileDoc.getFields());
        dataModel.put("importItems", protocolFileDoc.getImportItems());
        dataModel.put("importItems2", protocolFileDoc.getImportItems2());
        // 如果是Response类型，使用Message作为基类
        String baseClass = "Message";
        if (Response.class.isAssignableFrom(protocolFileDoc.getClazz())) {
            baseClass = "Response";
        } else {
            if (protocolFileDoc.getClazz().getSuperclass() != Object.class) {
                baseClass = protocolFileDoc.getClazz().getSuperclass().getSimpleName();
            }
        }
        dataModel.put("baseClass", baseClass);
        if (Message.class.isAssignableFrom(protocolFileDoc.getClazz())) {
            dataModel.put("cmd", protocolFileDoc.getCmd());
        }
        // 用于存储生成的TypeScript代码的字符串输出流
        FileWriter out = new FileWriter(newDirectory + "/" + protocolFileDoc.getName() + ".cs");
        // 处理模板，将数据模型应用到模板上进行生成
        template.process(dataModel, out);
    }


    private static String fieldOfTypeScript(Field field) {
        Class<?> type = field.getType();
        Class<?> eleType = null;
        if (Collection.class.isAssignableFrom(type)) {
            eleType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            return "List<" + javaType2CSharpType(eleType) + ">";
        } else if (type.isArray()) {
            eleType = type.getComponentType();
            return "List<" + javaType2CSharpType(eleType) + ">";
        } else if (Map.class.isAssignableFrom(type)) {
            // 处理Map/HashMap类型（转为Dictionary）
            ParameterizedType mapType = (ParameterizedType) field.getGenericType();
            // 获取Map的两个泛型参数：键类型和值类型
            Class<?> keyType = (Class<?>) mapType.getActualTypeArguments()[0];
            Class<?> valueType = (Class<?>) mapType.getActualTypeArguments()[1];
            // 转换为C#的Dictionary<键类型, 值类型>
            return "Dictionary<" + javaType2CSharpType(keyType) + ", " + javaType2CSharpType(valueType) + ">";
        }
        return javaType2CSharpType(type);
    }

    private static String javaType2CSharpType(Class<?> type) {
        if (type == boolean.class || type == Boolean.class) {
            return "bool";
        } else if (type == byte.class || type == Byte.class) {
            return "byte";
        } else if (type == short.class || type == Short.class) {
            return "int";
        } else if (type == int.class || type == Integer.class) {
            return "int";
        } else if (type == long.class || type == Long.class) {
            return "long";
        } else if (type == String.class) {
            return "string";
        }
        return type.getSimpleName();
    }

}
