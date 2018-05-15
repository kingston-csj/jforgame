package com.kingston.jforgame.server.game.activity.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.activity.ActivityDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module=Modules.ACTIVITY, cmd=ActivityDataPool.REQ_OPEN_PANEL)
public class ReqOpenActivityPanelMessage extends Message {
	
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
