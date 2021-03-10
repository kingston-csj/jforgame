
package jforgame.server.game;

public interface Modules {

	// ------------------底层功能支持模块（从0到100）-----------------

	short BASE = 1;

	short GM = 2;

	short NOTICE = 3;

	// ------------------业务功能模块（101开始）---------------------

	/** 登录 */
	short LOGIN = 101;
	/** 玩家 */
	short PLAYER = 102;
	/** 场景 */
	short SCENE = 103;
	/** 活动 */
	short ACTIVITY = 104;
	/** 技能 */
	short SKILL = 105;
	/** 聊天 */
	short CHAT = 106;

	// ------------------跨服业务功能模块（300开始）---------------------
	/** 跨服基础 */
	short CROSS = 300;
	/** 跨服玩法 */
	short CROSS_BUSINESS = 301;

}
