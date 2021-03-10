package jforgame.server.game.gm.command;

import java.util.List;

import jforgame.server.doctor.HotswapManager;
import jforgame.server.game.database.user.PlayerEnt;
import jforgame.server.game.gm.message.ResGmResult;

public class HotSwapCommand extends AbstractGmCommand {

	@Override
	public String getPattern() {
		return "^hotSwap\\s+([a-zA-Z_0-9]+)";
	}

	@Override
	public String help() {
		return "热更代码(^hotSwap [dirtory])";
	}

	@Override
	public ResGmResult execute(PlayerEnt player, List<String> params) {
		String path = params.get(0);
		String execResult = HotswapManager.INSTANCE.reloadClass(path);
		return ResGmResult.buildSuccResult(execResult);
	}

}