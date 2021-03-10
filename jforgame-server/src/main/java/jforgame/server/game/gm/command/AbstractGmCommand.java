package jforgame.server.game.gm.command;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jforgame.server.game.database.user.PlayerEnt;
import jforgame.server.game.gm.message.ResGmResult;

/**
 * 抽象gm命令
 * @author kinson
 */
public abstract class AbstractGmCommand {
	
	/**
	 * 正则表达式模式
	 * @return
	 */
	public abstract String getPattern();
	
	/**
	 * 帮助文档
	 * @return
	 */
	public abstract String help();
	
	/**
	 * 是否匹配
	 * @param pattern
	 * @param content
	 * @return
	 */
	public boolean isMatch(Pattern pattern, Matcher matcher, String content) {
		return matcher.matches();
	}
	
	/**
	 * 返回正则表达式解析的一系列参数
	 * @param matcher
	 * @param message
	 * @return
	 */
	public List<String> params(Matcher matcher, String message) {
		List<String> params = new ArrayList<>();
		for (int i=1; i<matcher.groupCount()+1; i++) {
			params.add(matcher.group(i));
		}
		return params;
	}
	
	/**
	 * 执行逻辑
	 * @param player
	 * @param params
	 * @return
	 */
	public abstract ResGmResult execute(PlayerEnt player, List<String> params);
	
	

}
