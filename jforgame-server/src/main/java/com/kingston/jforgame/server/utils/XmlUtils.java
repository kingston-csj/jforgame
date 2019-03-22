package com.kingston.jforgame.server.utils;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * xml相关工具栏
 * @author kingston
 */
public class XmlUtils {
	
	private static Logger logger = LoggerFactory.getLogger(XmlUtils.class.getSimpleName());
	
	private static final String ROOT_CONFIG_PATH = "configs" + File.separator;
	
	public static Document loadConfigFile(String cfgFile) {
		Document dom = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			dom = db.parse(cfgFile);
		} catch (ParserConfigurationException | SAXException pce) {
			logger.error("Parse xml config file error.", pce);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			logger.error("Can't open configuration file.", ioe);
		}
		return dom;
	}
	
	/**
	 * 读取xml配置文件的根节点（注意文件的配置路径）
	 * @param configFile
	 * @return
	 */
	public static Element loadConfigRootElement(String configFile) {
		//必须在JavaBuildPath-->Source 路径把 src/main/resource的输出目录设 跟项目src同级别的configs目录下
		Document configDoc = loadConfigFile(ROOT_CONFIG_PATH + configFile);
		return configDoc.getDocumentElement();
	}
	
	 public static <T> T createConfigXml(String fileName, Class<T> configClass) {
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
