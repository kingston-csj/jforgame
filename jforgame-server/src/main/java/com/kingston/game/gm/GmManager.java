package com.kingston.game.gm;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.kingston.game.database.user.player.Player;
import com.kingston.game.gm.command.AbstractGmCommand;
import com.kingston.game.gm.message.ResGmResultMessage;
import com.kingston.game.player.PlayerManager;
import com.kingston.net.message.MessagePusher;
import com.kingston.utils.ClassFilter;
import com.kingston.utils.ClassScanner;

public class GmManager {

	private static volatile GmManager instance;

	private GmManager() {}

	/** 缓存ｇｍ指令的模式与对应的逻辑处理者 */
	private Map<Pattern, AbstractGmCommand> commands = new HashMap<>();
	
	private final String SCAN_PATH = "com.kingston.game.gm.command";

	public static GmManager getInstance() {
		if (instance == null) {
			synchronized(GmManager.class) {
				if (instance == null) {
					instance = new GmManager();
					instance.init();
				}
			}
		}
		return instance;
	}

	private void init() {
		Set<Class<?>> clazzs = ClassScanner.listAllSubclasses(SCAN_PATH, AbstractGmCommand.class);

		for (Class<?> clazz:clazzs) {
			try{
				AbstractGmCommand command = (AbstractGmCommand) clazz.newInstance();
				String regex = command.getPattern();
				commands.put(Pattern.compile(regex), command);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	/**
	 * 处理gm入口
	 * @param playerId
	 * @param content
	 * @return
	 */
	public void receiveCommand(long playerId, String content) {
		Player player = PlayerManager.getInstance().get(playerId);
		//判断权限
		if (!hasExecPower(player)) {
			return;
		}
		for (Map.Entry<Pattern, AbstractGmCommand> entry:commands.entrySet()) {
			Pattern pattern = entry.getKey(); 
			AbstractGmCommand command = entry.getValue();

			Matcher matcher = pattern.matcher(content);
			if (command.isMatch(pattern, matcher, content)) {
				List<String> params = command.params(matcher, content);
				ResGmResultMessage result =  command.execute(player, params);
				MessagePusher.pushMessage(playerId, result);
				return;
			}
		}

		ResGmResultMessage failedMessage = ResGmResultMessage.buildFailResult("找不到对应的gm命令");
		MessagePusher.pushMessage(playerId, failedMessage);
	}
	
	/**
	 * 是否有执行权限
	 * @param player
	 * @return
	 */
	private boolean hasExecPower(Player player) {
		//这里根据具体业务进行拦截
		return true;
	}
	
	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("^reloadConfig\\s+([a-zA-Z_]+)");
		String expr = "reloadConfig CofingPlayer_Level";
		Matcher matcher = pattern.matcher(expr);
		if (matcher.matches()) {
			List<String> params = new ArrayList<>();
			for (int i=1; i<matcher.groupCount()+1; i++) {
				params.add(matcher.group(i));
			}
			System.err.println(params);
		}
		
	}
	
}
