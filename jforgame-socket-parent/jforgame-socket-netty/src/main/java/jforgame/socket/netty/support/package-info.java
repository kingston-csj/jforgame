/**
 * support目录提供绝大部分NettySocket开发基础组件的默认实现，包括服务端和客户端。
 * 由于Netty只是一个纯NIO网络框架，考虑到客户端代码对于私有协议栈的设计，消息编解码，io链式处理差异性比较大，
 * 多样性主要在于{@link io.netty.bootstrap.ServerBootstrap#childHandler(ChannelHandler)}的接口非常灵活，
 * 在框架里不适合设计得太拘束，应该往宽松的方向设计。当然，你也可以使用大部分接口的默认实现，这对于实现一个游戏
 * 网络框架已足矣。
 * 如果你需要增加消息的加解密，或者其他扩展，可优先选择后门钩子 {@link jforgame.socket.netty.support.server.ExtendedChannelHandler}
 *
 */
package jforgame.socket.netty.support;

import io.netty.channel.ChannelHandler;