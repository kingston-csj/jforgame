/**
 * Provides default implementations of most basic components for NettySocket development, including server and client.
 * Since Netty is just a pure NIO network framework, considering the differences in private protocol stack design,
 * message encoding/decoding, and IO chain processing in client code,
 * the diversity mainly comes from the flexibility of the {@link io.netty.bootstrap.ServerBootstrap#childHandler(ChannelHandler)} interface,
 * the framework should not be designed too restrictively, but should be designed in a more relaxed direction.
 * Of course, you can also use the default implementations of most interfaces, which is sufficient for implementing a game
 * network framework.
 * If you need to add message encryption/decryption or other extensions, you can preferably use the post-hook {@link jforgame.socket.netty.server.ExtendedChannelHandler}
 * If you want to modify the private protocol stack, it is recommended to use class override to replace the framework's own class definitions (mainly replacing DefaultProtocolDecoder and DefaultProtocolEncoder)
 */
package jforgame.socket.netty;

import io.netty.channel.ChannelHandler;