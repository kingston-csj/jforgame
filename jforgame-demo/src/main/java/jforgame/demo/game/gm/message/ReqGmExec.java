package jforgame.demo.game.gm.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.demo.game.Modules;
import jforgame.demo.game.gm.GmConstant;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

/**
 * gm 执行命令
 */
@MessageMeta(module=Modules.GM, cmd=GmConstant.REQ_GM_EXEC)
public class ReqGmExec implements Message {

	/**
	 * gm命令
	 * 格式：例如 level 10
	 */
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
