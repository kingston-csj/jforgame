package com.kingston.jforgame.server.game.player.serializer;

import org.apache.commons.lang3.StringUtils;

import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.function.model.Function;
import com.kingston.jforgame.server.utils.JsonUtils;

public class FunctionPropSerializer implements IPlayerPropSerializer {

	@Override
	public void serialize(Player player) {
		Function functions = player.getFunction();
		if (functions != null) {
			player.setFunctionJson(JsonUtils.object2String(functions));
		}
	}

	@Override
	public void deserialize(Player player) {
		String json = player.getFunctionJson();
		if (!StringUtils.isEmpty(json)) {
			player.setFunction(JsonUtils.string2Object(json, Function.class));
		} else {
			player.setFunction(new Function());
		}
	}

}
