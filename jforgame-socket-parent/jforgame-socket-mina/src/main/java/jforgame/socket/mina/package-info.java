/**
 * Provides default implementations of most basic components for NettySocket development, including server and client.
 * Since Netty is just a pure NIO network framework, considering the differences in private protocol stack design,
 * message encoding/decoding, and IO chain processing in client code are quite large.
 * The diversity is mainly because the interface of {@link org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder} is very flexible.
 * It is not suitable to design too restrictively in the framework, it should be designed in a more relaxed direction.
 * Of course, you can also use the default implementations of most interfaces, which is sufficient for implementing a game
 * network framework.
 * If you wish to modify the private protocol stack or add message encryption/decryption, it is recommended to use class
 * overriding to replace the framework's own class definitions (mainly replacing DefaultProtocolDecoder and DefaultProtocolEncoder)
 */
package jforgame.socket.mina;