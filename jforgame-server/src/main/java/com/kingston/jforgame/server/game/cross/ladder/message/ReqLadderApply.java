package com.kingston.jforgame.server.game.cross.ladder.message;

import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.cross.ladder.service.LadderDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module = Modules.CROSS, cmd = LadderDataPool.REQ_LADDER_APPLY)
public class ReqLadderApply extends Message {

}
