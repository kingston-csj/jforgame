package jforgame.server.game.gm.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.server.game.Modules;
import jforgame.server.game.gm.GmConstant;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module=Modules.GM, cmd=GmConstant.REQ_GM_EXEC)
public class ReqGmExec extends Message {
	
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
