package com.kingston.net.context;

/**
 * timer task, for thread safe
 * @author kingston
 */
public abstract class TimerTask extends AbstractDistributeTask {

	private int currLoop;
	/** indicate loop task when it is smaller than 0*/
	private int maxLoop;

	public TimerTask(int distributeKey) {
		this(distributeKey, 1);
	}

	public TimerTask(int distributeKey, int maxLoop) {
		this.distributeKey = distributeKey;
		this.maxLoop = maxLoop;
	}

	public void updateLoopTimes() {
		this.currLoop += 1;
	}

	public boolean canRunAgain() {
		if (this.maxLoop <= 0) {
			return true;
		}
		return this.currLoop < this.maxLoop;
	}

}
