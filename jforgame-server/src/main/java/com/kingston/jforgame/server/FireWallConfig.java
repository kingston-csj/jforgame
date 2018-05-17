package com.kingston.jforgame.server;

/**
 * 防火墙配置
 * @author kingston
 *
 */
public class FireWallConfig {
	
	/** 每秒最大收包数量 */
	private int maxPackagePerSecond;

	public int getMaxPackagePerSecond() {
		return maxPackagePerSecond;
	}

	public void setMaxPackagePerSecond(int maxPackagePerSecond) {
		this.maxPackagePerSecond = maxPackagePerSecond;
	}

	@Override
	public String toString() {
		return "FireWallConfig [maxPackagePerSecond=" + maxPackagePerSecond + "]";
	}

}
