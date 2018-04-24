package com.kingston.jforgame.server.game.crossrank.impl;

import com.kingston.jforgame.server.game.crossrank.AbstractCrossRank;
import com.kingston.jforgame.server.game.crossrank.CrossRankKinds;

/**
 *  cross server level rank 
 * @author kingston
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
