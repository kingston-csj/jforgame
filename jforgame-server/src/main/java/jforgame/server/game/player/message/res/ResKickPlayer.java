package jforgame.server.game.player.message.res;

import jforgame.server.game.Modules;
import jforgame.server.game.player.PlayerDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.RES_KICK_PLAYER)
public class ResKickPlayer extends Message {

}
