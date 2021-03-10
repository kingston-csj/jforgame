/**
 * 跨服基础
 * 服务器与服务器之间"点对点通信"，类似于客户端与游戏服通信
 * 游戏服可以主动向跨服发送消息，跨服也可以主动向游戏服发送消息
 * 因此单纯依靠 Req表示请求消息，Res表示响应消息已无意义
 * 为了清晰表示跨服数据流向，游戏服使用前缀G（Game），跨服使用前缀F（Fight）
 * 游戏服发给战斗服的协议，以G2F作为前缀，{@link jforgame.server.cross.core.callback.G2FCallBack}
 * 战斗服发给游戏服的协议，以F2G作为前缀，{@link jforgame.server.cross.core.callback.F2GCallBack}
 */
package jforgame.server.cross.core;
