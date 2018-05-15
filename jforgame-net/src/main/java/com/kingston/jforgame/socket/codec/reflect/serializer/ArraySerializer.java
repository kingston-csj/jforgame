package com.kingston.jforgame.socket.codec.reflect.serializer;

import java.lang.reflect.Array;

import org.apache.mina.core.buffer.IoBuffer;

import com.kingston.jforgame.socket.utils.ByteBuffUtil;
import com.kingston.jforgame.socket.utils.ReflectUtil;

/**
* 
* 数组属性序列化
* 注：由于数组元素bean没有像Message一样注册id，
* 因此数组的元素不能是父类或抽象类
*/
public class ArraySerializer extends Serializer {

	@Override
	public Object decode(IoBuffer in, Class<?> type, Class<?> wrapper) {
		int size = ByteBuffUtil.readShort(in);

		Object array = ReflectUtil.newArray(type, wrapper, size);

		for (int i=0;i<size; i++) {
			Serializer fieldCodec = Serializer.getSerializer(wrapper);
			Object eleValue = fieldCodec.decode(in, wrapper, null);
			Array.set(array, i, eleValue);
		}

		return array;
	}

	@Override
	public void encode(IoBuffer out, Object value, Class<?> wrapper) {
		if (value == null) {
			ByteBuffUtil.writeShort(out, (short)0);
			return;
		}
		int size = Array.getLength(value);
		ByteBuffUtil.writeShort(out, (short)size);
		for (int i=0; i<size; i++) {
			Object elem = Array.get(value, i);
			Class<?> clazz = elem.getClass();
			Serializer fieldCodec = Serializer.getSerializer(clazz);
			fieldCodec.encode(out, elem, wrapper);
		}
	}

}
