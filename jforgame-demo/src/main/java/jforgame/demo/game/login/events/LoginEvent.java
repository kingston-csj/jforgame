package jforgame.demo.game.login.events;

import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.player.events.PlayerEvent;

public class LoginEvent extends PlayerEvent {

	public LoginEvent(PlayerEnt player) {
		super(player);
	}

}
