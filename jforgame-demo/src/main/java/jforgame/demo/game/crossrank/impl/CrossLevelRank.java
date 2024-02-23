package jforgame.demo.game.crossrank.impl;

import jforgame.demo.game.crossrank.AbstractCrossRank;
import jforgame.demo.game.crossrank.CrossRankKinds;

/**
 *  cross server level rank 
 * @author kinson
 *
 */
public class CrossLevelRank extends AbstractCrossRank {
	

	public CrossLevelRank() {
		// just for jprotobuf
	}

	public CrossLevelRank(long playerId, int score) {
		super(playerId, score);
	}

	@Override
	public int getRankType() {
		return  CrossRankKinds.LEVEL;
	}

}
