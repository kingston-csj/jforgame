package jforgame.demo;

/**
 * 防火墙配置
 *
 */
public class FireWall {

	/** 每秒最大收包数量 */
	private int maxPackagePerSecond;

	/** XX秒为洪水窗口期 */
	private int floodWindowSeconds;

	/** 窗口期超过多少次即判定有效 */
	private int maxFloodTimes;


	public int getMaxPackagePerSecond() {
		return maxPackagePerSecond;
	}

	public void setMaxPackagePerSecond(int maxPackagePerSecond) {
		this.maxPackagePerSecond = maxPackagePerSecond;
	}

	public int getFloodWindowSeconds() {
		return floodWindowSeconds;
	}

	public void setFloodWindowSeconds(int floodWindowSeconds) {
		this.floodWindowSeconds = floodWindowSeconds;
	}

	public int getMaxFloodTimes() {
		return maxFloodTimes;
	}

	public void setMaxFloodTimes(int maxFloodTimes) {
		this.maxFloodTimes = maxFloodTimes;
	}

	@Override
	public String toString() {
		return "FireWallConfig [maxPackagePerSecond=" + maxPackagePerSecond + ", floodWindowSeconds="
				+ floodWindowSeconds + ", maxFloodTimes=" + maxFloodTimes + "]";
	}
}
