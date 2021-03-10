package jforgame.merge.utils;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class XmlUtils {

    private static Logger logger = LoggerFactory.getLogger(XmlUtils.class.getSimpleName());

    private static final String ROOT_CONFIG_PATH = "configs" + File.separator;

    /**
     * 读取xml配置文件
     *
     * @param fileName
     * @param configClass
     * @return
     */
    public static <T> T loadXmlConfig(String fileName, Class<T> configClass) {
        T ob = null;
        fileName = ROOT_CONFIG_PATH + fileName;
        if (!new File(fileName).exists()) {
            return ob;
        }
        Serializer serializer = new Persister();
        try {
            ob = serializer.read(configClass, new File(fileName));
        } catch (Exception ex) {
            logger.error("文件" + fileName + "配置有误", ex);
        }
        return ob;
    }

}