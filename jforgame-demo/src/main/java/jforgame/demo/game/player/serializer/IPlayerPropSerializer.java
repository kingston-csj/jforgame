package jforgame.demo.game.player.serializer;

import jforgame.demo.game.database.user.PlayerEnt;

import java.io.Serializable;

public interface IPlayerPropSerializer extends Serializable {

    void serialize(PlayerEnt player);

    void deserialize(PlayerEnt player);

}
