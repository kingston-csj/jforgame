package jforgame.demo.game.notice.message;

import java.util.ArrayList;
import java.util.List;

import jforgame.demo.game.Modules;
import jforgame.demo.game.notice.NoticeDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module = Modules.NOTICE, cmd = NoticeDataPool.RES_NOTICE)
public class ResSystemNotice implements Message {

	private int noticeId;

	private List<String> stringParams = new ArrayList<>();

	private List<Integer> numberParams = new ArrayList<>();

	public static ResSystemNotice valueOf(int noticeId) {
		ResSystemNotice notice = new ResSystemNotice();
		notice.noticeId = noticeId;

		return notice;
	}

	public void add(String content) {
		stringParams.add(content);
	}

	public void add(Integer number) {
		numberParams.add(number);
	}

	public int getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(int noticeId) {
		this.noticeId = noticeId;
	}

	public List<String> getStringParams() {
		return stringParams;
	}

	public void setStringParams(List<String> stringParams) {
		this.stringParams = stringParams;
	}

	public List<Integer> getNumberParams() {
		return numberParams;
	}

	public void setNumberParams(List<Integer> numberParams) {
		this.numberParams = numberParams;
	}

	@Override
	public String toString() {
		return "ResSystemNotice [noticeId=" + noticeId + "]";
	}

}
