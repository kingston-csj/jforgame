package jforgame.server.game.cross.ladder.message;

import jforgame.server.game.Modules;
import jforgame.server.game.cross.ladder.service.LadderDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module = Modules.CROSS_BUSINESS, cmd = LadderDataPool.REQ_LADDER_APPLY)
public class ReqLadderApply extends Message {

}
