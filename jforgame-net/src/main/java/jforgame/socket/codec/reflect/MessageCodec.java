package jforgame.socket.codec.reflect;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 消息或vo的解析器
 * 
 * @author kinson
 */
public class MessageCodec extends Codec {

	private List<FieldCodecMeta> fieldsMeta;

	public static MessageCodec valueOf(List<FieldCodecMeta> fieldsMeta) {
		MessageCodec serializer = new MessageCodec();
		serializer.fieldsMeta = fieldsMeta;
		return serializer;
	}

	@Override
	public Object decode(ByteBuffer in, Class<?> type, Class<?> wrapper) {
		try {
			Object bean = type.newInstance();
			for (FieldCodecMeta fieldMeta : fieldsMeta) {
				Field field = fieldMeta.getField();
				Codec fieldCodec = fieldMeta.getCodec();
				Object value = fieldCodec.decode(in, fieldMeta.getType(), fieldMeta.getWrapper());
				field.set(bean, value);
			}
			return bean;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void encode(ByteBuffer out, Object message, Class<?> wrapper) {
		try {
			for (FieldCodecMeta fieldMeta : fieldsMeta) {
				Field field = fieldMeta.getField();
				Codec fieldCodec = Codec.getSerializer(fieldMeta.getType());
				Object value = field.get(message);
				fieldCodec.encode(out, value, fieldMeta.getWrapper());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
