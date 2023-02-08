//package jforgame.socket.codec;
//
//import com.sun.xml.internal.messaging.saaj.soap.MessageFactoryImpl;
//import jforgame.socket.message.MessageDecoder;
//import jforgame.socket.message.MessageEncoder;
//import jforgame.socket.message.MessageFactory;
//
///**
// * @author kinson
// */
//public class SerializerHelper {
//
//	public static volatile SerializerHelper instance;
//
//	/**
//	 * 消息序列化编解码
//	 */
//	private static MessageCodecFactory serializerFactory = new StructPrivateProtocolCodec(MessageFactoryImpl.);
//
//	public static SerializerHelper getInstance() {
//		if (instance != null) {
//			return instance;
//		}
//		synchronized (SerializerHelper.class) {
//			if (instance == null) {
//				SerializerHelper self =  new SerializerHelper();
//				instance = self;
//			}
//		}
//		return instance;
//	}
//
//
//	public MessageDecoder getDecoder() {
//		return serializerFactory.getDecoder();
//	}
//
//	public MessageEncoder getEncoder() {
//		return serializerFactory.getEncoder();
//	}
//
//	public MessageCodecFactory getSerializerFactory() {
//		return serializerFactory;
//	}
//
//}
