package com.kingston.jforgame.server.game.database.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.kingston.jforgame.common.utils.ClassScanner;
import com.kingston.jforgame.server.logs.LoggerUtils;

/**
 * 所有策划配置的数据池
 * 
 * @author kingston
 */
public class ConfigDatasPool {

	private static ConfigDatasPool instance = new ConfigDatasPool();

	private ConfigDatasPool() {
	}

	public static ConfigDatasPool getInstance() {
		return instance;
	}

	private ConcurrentMap<Class<?>, Reloadable> datas = new ConcurrentHashMap<>();

	/**
	 * 起服读取所有的配置数据
	 */
	public void loadAllConfigs() {
		Set<Class<?>> clazzs = ClassScanner.listAllSubclasses("com.kingston.jforgame.server.game.database.config",
				Reloadable.class);

		clazzs.forEach(c -> {
			try {
				Reloadable container = (Reloadable) c.newInstance();
				container.reload();
				datas.put(c, container);
			} catch (Exception e) {
				LoggerUtils.error(c.getName() + "策划配置数据有误，请检查", e);
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
