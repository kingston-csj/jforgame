package com.kingston.game.gm.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.game.Modules;
import com.kingston.game.gm.GmConstant;
import com.kingston.net.Message;
import com.kingston.net.annotation.MessageMeta;

@MessageMeta(module=Modules.GM, cmd=GmConstant.REQ_GM_EXEC)
public class ReqGmExecMessage extends Message {
	
	@Protobuf(order = 1)
	public String command;

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	@Override
	public String toString() {
		return "ReqGmExecMessage [command=" + command + "]";
	}
	
	


}
