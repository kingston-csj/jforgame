package jforgame.server.game.database.user;

import jforgame.server.thread.ThreadCenter;
import jforgame.orm.converter.Convert;
import jforgame.server.db.BaseEntity;
import jforgame.server.db.JsonAttributeConverter;
import jforgame.server.game.login.model.Platform;
import jforgame.server.game.vip.model.VipRight;
import jforgame.socket.actor.MailBox;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * 玩家实体
 * @author kinson
 */
@Entity(name = "playerent")
public class PlayerEnt extends BaseEntity {

	private static final long serialVersionUID = 8913056963732639062L;

	@Id
	@Column
	private Long id;
	
	@Column
	private long accountId;

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

	@Column
	@Convert(converter = JsonAttributeConverter.class)
	private VipRight vipRight;

	@Column(columnDefinition="varchar(16)")
	private Platform platform;

	public PlayerEnt() {
	}

	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public VipRight getVipRight() {
		return vipRight;
	}

	public void setVipRight(VipRight vipRight) {
		this.vipRight = vipRight;
	}

	@Override
	public void doAfterInit() {
	}

	@Override
	public void doBeforeSave() {
	}

	@Override
	public MailBox mailBox() {
		return ThreadCenter.createBusinessMailBox("player");
	}

}
