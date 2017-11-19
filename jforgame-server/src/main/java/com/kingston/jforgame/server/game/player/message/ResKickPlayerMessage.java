package com.kingston.jforgame.server.game.player.message;

import com.kingston.jforgame.net.socket.annotation.MessageMeta;
import com.kingston.jforgame.net.socket.message.Message;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.player.PlayerDataPool;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.RES_KICK_PLAYER)
public class ResKickPlayerMessage extends Message {

}
