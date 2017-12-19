package com.kingston.jforgame.server.game.player.serializer;

import org.apache.commons.lang3.StringUtils;

import com.kingston.jforgame.server.db.DbService;
import com.kingston.jforgame.server.game.database.user.player.Player;
import com.kingston.jforgame.server.game.vip.model.VipRight;
import com.kingston.jforgame.server.utils.JsonUtils;

public class VipPropSerializer implements IPlayerPropSerializer {

	@Override
	public void serialize(Player player) {
		VipRight vipRight = player.getVipRight();
		if (vipRight != null) {
			player.setVipRightJson(JsonUtils.object2String(vipRight));
		}
	}

	@Override
	public void deserialize(Player player) {
		String json = player.getVipRightJson();
		if (!StringUtils.isEmpty(json)) {
			player.setVipRight(JsonUtils.string2Object(json, VipRight.class));
		} else {
			player.setVipRight(new VipRight());
		}
	}

}
