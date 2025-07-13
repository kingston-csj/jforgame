package jforgame.codec.struct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * 消息或vo的解析器
 *
 */
public class BeanCodec extends Codec {

	private List<FieldCodecMeta> fieldsMeta;

	private static final Logger logger = LoggerFactory.getLogger(ArrayCodec.class);

	public static BeanCodec valueOf(List<FieldCodecMeta> fieldsMeta) {
		BeanCodec serializer = new BeanCodec();
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
			logger.error("", e);
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
			logger.error("", e);
		}
	}

}
