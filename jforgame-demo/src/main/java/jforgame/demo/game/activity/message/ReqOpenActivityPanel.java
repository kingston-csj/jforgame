package jforgame.demo.game.activity.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.activity.ActivityDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module=Modules.ACTIVITY, cmd=ActivityDataPool.REQ_OPEN_PANEL)
public class ReqOpenActivityPanel implements Message {
	
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
