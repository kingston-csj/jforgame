package com.kingston.jforgame.server.game.player.serializer;

import com.kingston.jforgame.server.game.database.user.player.Player;

import java.io.Serializable;

public interface IPlayerPropSerializer extends Serializable {

    void serialize(Player player);

    void deserialize(Player player);

}
