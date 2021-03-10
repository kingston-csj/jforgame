package jforgame.server.net.model;

public class FloodRecord {

	/** 上一秒累计收包数量 */
	private int receivedPacksLastSecond;
	
	/** 上一次收包时间戳 */
	private long lastReceivedTime;
	
	/** 单位时间内累计的洪水记录 */
	private int floodTimes;
	
	private long lastFloodTime;

	public int getReceivedPacksLastSecond() {
		return receivedPacksLastSecond;
	}

	public void setReceivedPacksLastSecond(int receivedPacksLastSecond) {
		this.receivedPacksLastSecond = receivedPacksLastSecond;
	}
	
	public int addSecondReceivedPackage() {
		return ++ this.receivedPacksLastSecond ;
	}

	public long getLastReceivedTime() {
		return lastReceivedTime;
	}

	public void setLastReceivedTime(long lastReceivedTime) {
		this.lastReceivedTime = lastReceivedTime;
	}

	public int getFloodTimes() {
		return floodTimes;
	}

	public void setFloodTimes(int floodTimes) {
		this.floodTimes = floodTimes;
	}
	
	public int addFloodTimes() {
		return ++this.floodTimes;
	}

	public long getLastFloodTime() {
		return lastFloodTime;
	}

	public void setLastFloodTime(long lastFloodTime) {
		this.lastFloodTime = lastFloodTime;
	}
	
}
