package jforgame.demo.game.player.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.player.PlayerDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module=Modules.PLAYER, cmd= PlayerDataPool.RES_KICK_PLAYER)
public class ResKickPlayer implements Message {

}
