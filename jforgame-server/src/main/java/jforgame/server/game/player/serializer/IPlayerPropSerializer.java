package jforgame.server.game.player.serializer;

import jforgame.server.game.database.user.PlayerEnt;

import java.io.Serializable;

public interface IPlayerPropSerializer extends Serializable {

    void serialize(PlayerEnt player);

    void deserialize(PlayerEnt player);

}
