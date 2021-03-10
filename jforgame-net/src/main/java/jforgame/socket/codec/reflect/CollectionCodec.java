package jforgame.socket.codec.reflect;

import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jforgame.socket.utils.ByteBuffUtil;

/**
* 
* 集合属性序列化
* 注：由于集合元素bean没有像Message一样注册id，
* 因此集合的元素不能是父类或抽象类
*/
public class CollectionCodec extends Codec {

	@Override
	public Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		int size = ByteBuffUtil.readShort(in);
		int modifier = type.getModifiers();
		Collection<Object> result = null;

		if (Modifier.isAbstract(modifier) || Modifier.isInterface(modifier)) {
			if (List.class.isAssignableFrom(type)) {
				result = new ArrayList<>();
			} else if (Set.class.isAssignableFrom(type)) {
				result = new HashSet<>();
			}
		} else {
			try {
				result = (Collection)type.newInstance();
			}catch(Exception e) {
				e.printStackTrace();
				result = new ArrayList<>();
			}
		}

		for (int i=0; i<size; i++) {
			Codec fieldCodec = getSerializer(wrapper);
			Object eleValue = fieldCodec.decode(in, wrapper, null);
			result.add(eleValue);
		}

		return result;
	}

	@Override
	public void encode(ByteBuffer out, Object value, Class<?> wrapper) {
		if (value == null) {
			ByteBuffUtil.writeShort(out, (short)0);
			return;
		}
		Collection<Object> collection = (Collection)value;
		int size = collection.size();
		ByteBuffUtil.writeShort(out, (short)size);

		for (Object elem:collection) {
			Class<?> clazz = elem.getClass();
			Codec fieldCodec = getSerializer(clazz);
			fieldCodec.encode(out, elem, wrapper);
		}
	}

}
