package jforgame.server.game.crossrank;

import jforgame.server.ServerConfig;

/**
 * provides a skeletal implementation of the <tt>CrossRank</tt>
 * @author kinson
 */
public abstract class AbstractCrossRank implements CrossRank {

	private int serverId;
	/** record creating timestamp */
	private long createTime;
	private long playerId;
	/** one level rank score */
	private int score;
	/** second level rank score */
	private int aid;
	/** 32位时间戳 */
	protected final long TIME_MAX_VALUE = 0xFFFFFFFFL;

	public AbstractCrossRank(long playerId, int score, int aid) {
		this.playerId = playerId;
		this.score = score;
		this.aid  = aid;
		this.serverId = ServerConfig.getInstance().getServerId();
		this.createTime = System.currentTimeMillis();
	}

	public AbstractCrossRank(long playerId, int score) {
		this(playerId, score, 0);
	}
	
	public AbstractCrossRank() {
		
	}

	@Override
	public int getServerId() {
		return serverId;
	}

	@Override
	public long getPlayerId() {
		return this.playerId;
	}

	@Override
	public long getCreateTime() {
		return createTime;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public int getAid() {
		return aid;
	}
	
	@Override
	public String buildRankKey() {
		return "CrossRank_" + getRankType();
	}
	
	@Override
	public String buildResultKey() {
		return getClass().getSimpleName() ;
	}
	
	@Override
	public double buildRankScore() {
		//default rank score 
		// score      |     createtime
		//  20bits            32bits
		long timePart = (TIME_MAX_VALUE - getCreateTime()/1000) & TIME_MAX_VALUE;
		long result  = (long)score << 32 | timePart;
		return  result;
	}

	@Override
	public String toString() {
		return "AbstractCrossRank [serverId=" + serverId
						+ ", createTime=" + createTime
						+ ", playerId=" + playerId
						+ ", score=" + score + ", aid="
						+ aid + "]";
	}

}
