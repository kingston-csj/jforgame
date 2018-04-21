package com.kingston.jforgame.server.game;

public interface Modules {

	//------------------底层功能支持模块（从0到100）-----------------

	int BASE = 0;

	int GM = 1;

	//------------------业务功能模块（101开始）---------------------

	/**　登录 */
	int LOGIN = 101;
	/** 玩家 */
	int PLAYER = 102;
	/** 场景 */
	int SCENE = 103;
	/** 活动 */
	int ACTIVITY = 104;
	/** 技能 */
	int SKILL = 105;


}
