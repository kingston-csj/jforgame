package com.kingston.jforgame.server.game.player.serializer;

import java.io.Serializable;

import com.kingston.jforgame.server.game.database.user.player.Player;

public interface IPlayerPropSerializer extends Serializable{

	void serialize(Player player);
	
	void deserialize(Player player);
	
}
