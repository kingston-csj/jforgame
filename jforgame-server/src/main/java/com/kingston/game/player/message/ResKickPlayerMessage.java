package com.kingston.game.player.message;

import com.kingston.game.Modules;
import com.kingston.game.player.PlayerDataPool;
import com.kingston.net.annotation.MessageMeta;
import com.kingston.net.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.RES_KICK_PLAYER)
public class ResKickPlayerMessage extends Message {

}
