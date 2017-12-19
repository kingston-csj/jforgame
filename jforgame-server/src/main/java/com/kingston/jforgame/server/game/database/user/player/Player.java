package com.kingston.jforgame.server.game.database.user.player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.net.socket.session.SessionManager;
import com.kingston.jforgame.net.socket.session.SessionProperties;
import com.kingston.jforgame.net.socket.task.IDistributable;
import com.kingston.jforgame.server.db.BaseEntity;
import com.kingston.jforgame.server.game.player.PlayerSerializerUtil;
import com.kingston.jforgame.server.game.vip.model.VipRight;
import com.kingston.jforgame.server.utils.IdGenerator;

/**
 * 玩家实体
 * @author kingston
 */
@Entity
public class Player extends BaseEntity implements IDistributable {

	private static final long serialVersionUID = 8913056963732639062L;

	@Id
	@Column
	private long id;

	@Column
	private String name;

	/**
	 * 职业
	 */
	@Column
	private int job;

	@Column
	private int level;

	@Column
	private long exp;
	
	/**
	 * 上一次每日重置的时间戳
	 */
	@Column
	private long lastDailyReset;

	private VipRight vipRight;
	
	@Column
	private String vipRightJson;

	public Player() {
		this.id = IdGenerator.getNextId();
	}

	@Override
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getJob() {
		return job;
	}

	public void setJob(int job) {
		this.job = job;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public long getLastDailyReset() {
		return lastDailyReset;
	}

	public void setLastDailyReset(long lastDailyReset) {
		this.lastDailyReset = lastDailyReset;
	}
	

	public VipRight getVipRight() {
		return vipRight;
	}

	public void setVipRight(VipRight vipRight) {
		this.vipRight = vipRight;
	}

	public String getVipRightJson() {
		return vipRightJson;
	}

	public void setVipRightJson(String vipRightJson) {
		this.vipRightJson = vipRightJson;
	}
	
	public void doAfterInit() {
		PlayerSerializerUtil.deserialize(this);
	}
	
	public void doBeforeSave() {
		PlayerSerializerUtil.serialize(this);
	}

	@Override
	public int distributeKey() {
		IoSession session = SessionManager.INSTANCE.getSessionBy(id);
		return (int)session.getAttribute(SessionProperties.DISTRIBUTE_KEY);
	}

	@Override
	public String toString() {
		return "Player [id=" + id + ", name=" + name + ", job=" + job + ", level=" + level + ", exp=" + exp
				+ ", lastDailyReset=" + lastDailyReset + "]";
	}

}
