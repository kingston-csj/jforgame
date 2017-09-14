package com.kingston.doctor.script;

import java.lang.reflect.Field;

import com.kingston.game.database.user.player.Player;
import com.kingston.game.player.PlayerManager;

public class CommonScript {

	public CommonScript() {
		//演示manager对象的热替换
		PlayerManager newMgr = new PlayerManager() {
			private String newField = "newField";
			private void sayHello() {
				System.err.println("hello---world-----");
			}

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
