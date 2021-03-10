package com.kinson.doctor.script;

import java.lang.reflect.Field;

import com.kinson.game.database.user.player.Player;
import com.kinson.game.player.PlayerManager;

public class CommonScript {

	public CommonScript() {
		nager newMgr = new nager() {
			//replace class, u can add private fields
			private String newField = "newField";
			//replace class, u can add private methods
			private void sayHello() {
				System.err.println("add new field succ, it's " + newField);
				System.err.println("add new method succ");
			}

			//replace public method, in order to fix bug in product environment
			@Override
			public Player load(Long playerId) throws Exception {
				sayHello();
				Player newPlayer = new Player();
				newPlayer.setId(playerId);
				newPlayer.setName("robot");
				return newPlayer;
			}
		};
		try{
			Field field = PlayerManager.class.getDeclaredField("instance");
			field.setAccessible(true);

			field.set(PlayerManager.getInstance(), newMgr);

			Player player = PlayerManager.getInstance().load(12345L);
			System.err.println(player.getName());
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}
