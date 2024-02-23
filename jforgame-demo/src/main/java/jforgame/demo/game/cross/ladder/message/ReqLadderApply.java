package jforgame.demo.game.cross.ladder.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.cross.ladder.service.LadderDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module = Modules.CROSS_BUSINESS, cmd = LadderDataPool.REQ_LADDER_APPLY)
public class ReqLadderApply implements Message {

}
