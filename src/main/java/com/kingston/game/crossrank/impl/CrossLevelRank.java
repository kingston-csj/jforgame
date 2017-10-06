package com.kingston.game.crossrank.impl;

import com.kingston.game.crossrank.AbstractCrossRank;

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
//		System.err.println("score=="+score+"|"+String.valueOf(buildRankScore()));
	}

}
