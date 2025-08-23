package jforgame.codec.protobuf;

import com.baidu.bjf.remoting.protobuf.Codec;
import com.baidu.bjf.remoting.protobuf.ProtobufIDLGenerator;
import com.baidu.bjf.remoting.protobuf.ProtobufProxy;
import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.codec.MessageCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * protobuf 消息编码解码器
 * 使用该编码器，服务端与客户端通过protobuf方式进行通信
 * 对于一个消息类，服务器仅需在消息类上添加{@link ProtobufClass}注解即可
 * 服务器可以通过{@link ProtobufIDLGenerator}生成对应的.proto文件
 */
public class ProtobufMessageCodec implements MessageCodec {

	private static final Logger logger = LoggerFactory.getLogger(ProtobufMessageCodec.class);

	@Override
	public Object decode(Class<?> msgClazz, byte[] body) {
		try {
			Codec<?> codec = ProtobufProxy.create(msgClazz);
            return codec.decode(body);
		} catch (IOException e) {
			logger.error("read message {} failed", msgClazz.getName(),e);
		}
		return null;
	}

	@Override
	public byte[] encode(Object message) {
		//写入具体消息的内容
		byte[] body = null;
		Class msgClazz = message.getClass();
		try {
			Codec<Object> codec = ProtobufProxy.create(msgClazz);
			body = codec.encode(message);
		} catch (Exception e) {
			logger.error("read message failed", e);
		}
		return body;
	}

}
