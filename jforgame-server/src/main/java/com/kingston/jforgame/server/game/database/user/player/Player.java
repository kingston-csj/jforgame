package com.kingston.jforgame.server.game.database.user.player;

import org.apache.mina.core.session.IoSession;

import com.kingston.jforgame.net.socket.session.SessionManager;
import com.kingston.jforgame.net.socket.session.SessionProperties;
import com.kingston.jforgame.net.socket.task.IDistributable;
import com.kingston.jforgame.server.db.BaseEntity;
import com.kingston.jforgame.server.utils.IdGenerator;
import com.kingston.orm.annotation.Column;
import com.kingston.orm.annotation.Entity;
import com.kingston.orm.annotation.Id;

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
