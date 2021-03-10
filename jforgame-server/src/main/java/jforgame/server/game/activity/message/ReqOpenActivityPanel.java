package jforgame.server.game.activity.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.server.game.Modules;
import jforgame.server.game.activity.ActivityDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module=Modules.ACTIVITY, cmd=ActivityDataPool.REQ_OPEN_PANEL)
public class ReqOpenActivityPanel extends Message {
	
	@Protobuf
	private int activityId;

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	@Override
	public String toString() {
		return "ReqOpenActivityPanelMessage [activityId=" + activityId + "]";
	}
	
}
