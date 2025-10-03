package jforgame.demo.game.database.config;

import jforgame.commons.util.ClassScanner;
import jforgame.demo.game.logger.LoggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 所有策划配置的数据池
 * 
 */
public class ConfigDataPool {

	private static ConfigDataPool instance = new ConfigDataPool();

	private ConfigDataPool() {
	}

	public static ConfigDataPool getInstance() {
		return instance;
	}

	private ConcurrentMap<Class<?>, Reloadable> datas = new ConcurrentHashMap<>();

	private Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 起服读取所有的配置数据
	 */
	public void loadAllConfigs() {
		String packName = ConfigDataPool.class.getPackage().getName();
		Set<Class<?>> clazzs = ClassScanner.listAllSubclasses(packName,
				Reloadable.class);

		clazzs.forEach(c -> {
			try {
				Reloadable container = (Reloadable) c.newInstance();
				container.reload();
				datas.put(c, container);
			} catch (Exception e) {
				logger.error(c.getName() + "策划配置数据有误，请检查", e);
				System.exit(0);
			}
		});
	}
	
	public <V> V getStorage(Class<?> config) {
		return (V) datas.get(config);
	}
	
	/**
	 * 单表重载
	 * @param configTableName 配置表名称
	 */
	public boolean reload(String configTableName) {
		for (Map.Entry<Class<?>, Reloadable> entry : datas.entrySet()) {
			Class<?> c = entry.getKey();
			if (c.getSimpleName().toLowerCase().indexOf(configTableName.toLowerCase()) >= 0) {
				try {
					Reloadable storage = (Reloadable) c.newInstance();
					storage.reload();
					datas.put(c, storage);
					return true;
				} catch (Exception e) {
					LoggerUtils.error(c.getName() + "配置数据重载异常", e);
				} 
				break;
			}
		}
		return false;
	}

}
