package jforgame.codec;

/**
 * Private protocol stack message decoder.
 * This only performs encoding and decoding on the message body itself, not including message headers.
 * Common message encoding and decoding methods include:
 * 1. json
 * 2. protobuf
 * 3. messagepack
 * 4. struct (custom, based on bean structure)
 */
public interface MessageCodec {

	/**
	 * 	Deserialize message based on message metadata.
	 *  body is already a complete message package, so the decoding buffer does not require complex operations, just use NIO's ByteBuff.
	 *
	 * @param clazz class of the message
	 * @param body  data body of the message
	 * @return request message
	 */
	Object decode(Class<?> clazz, byte[] body);

	/**
	 * Serialize a specific message to byte[]
	 * @param message message to encode
	 * @return byte array of the message
	 */
	byte[] encode(Object message);

}
