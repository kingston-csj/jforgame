package com.kingston.orm;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kingston.orm.annotation.Column;
import com.kingston.orm.annotation.Entity;
import com.kingston.orm.annotation.Id;
import com.kingston.orm.utils.StringUtils;
import com.kingston.utils.ClassFilter;
import com.kingston.utils.ClassScanner;

public enum OrmProcessor {

	INSTANCE;

	/** entity与对应的ormbridge的映射关系 */
	private Map<Class<?>, OrmBridge> classOrmMapperr = new HashMap<>();

	public void initOrmBridges() {
		Set<Class<?>> entityClazzs = listEntityClazzs();

		for (Class<?> clazz:entityClazzs) {
			OrmBridge bridge = createBridge(clazz);
			this.classOrmMapperr.put(clazz, bridge);
		}
	}

	private OrmBridge createBridge(Class<?> clazz) {
		OrmBridge bridge = new OrmBridge();
		Entity entity = (Entity) clazz.getAnnotation(Entity.class);
		//没有设置tablename,则用entity名首字母小写
		if (entity.table().length() <= 0) {
			bridge.setTableName(StringUtils.firstLetterToLowerCase(clazz.getSimpleName()));
		}else {
			bridge.setTableName(entity.table());
		}

		Field[] fields = clazz.getDeclaredFields();
		for (Field field:fields) {
			Column column = field.getAnnotation(Column.class);
			String fieldName = field.getName();
			try{
				if (column == null) {
					continue;
				}
				Method m = clazz.getMethod("get" + StringUtils.firstLetterToUpperCase(field.getName()));
				bridge.addGetterMethod(fieldName, m);
				Method m2 = clazz.getMethod("set" + StringUtils.firstLetterToUpperCase(field.getName()), field.getType());
				bridge.addSetterMethod(fieldName, m2);
				if (field.getAnnotation(Id.class) != null) {
					bridge.addUniqueKey(fieldName);
				}
				if (!StringUtils.isEmpty(column.name())) {
					bridge.addPropertyColumnOverride(fieldName, column.name());
				}
				bridge.addProperty(fieldName);
			}catch(Exception e) {
				throw new OrmConfigExcpetion(e);
			}
			//如果实体没有主键的话，一旦涉及更新，会影响整张表数据，后果是灾难性的
			//但readOnly的表只做查询，没这种限制
			if (!entity.readOnly() && bridge.getQueryProperties().size() <= 0) {
				throw new OrmConfigExcpetion(clazz.getSimpleName() + " entity 没有查询索引主键字段");
			}
		}

		return bridge;
	}

	private Set<Class<?>> listEntityClazzs() {
		return ClassScanner.getClasses("com.kingston.game", 
				new ClassFilter() {
			@Override
			public boolean accept(Class<?> clazz) {
				return clazz.getAnnotation(Entity.class) != null;
			}
		});
	}

	public OrmBridge getOrmBridge(Class<?> clazz) {
		return this.classOrmMapperr.get(clazz);
	}
}
