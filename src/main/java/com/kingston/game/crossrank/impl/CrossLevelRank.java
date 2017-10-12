package com.kingston.game.crossrank.impl;

import com.kingston.game.crossrank.AbstractCrossRank;
import com.kingston.game.crossrank.CrossRankKinds;

/**
 *  cross server level rank 
 * @author kingston
 *
 */
public class CrossLevelRank extends AbstractCrossRank {
	
	// just for jprotobuf
	public CrossLevelRank() {
		
	}

	public CrossLevelRank(long playerId, int score) {
		super(playerId, score);
	}
	
	public int getRankType() {
		return  CrossRankKinds.LEVEL;
	}

}
