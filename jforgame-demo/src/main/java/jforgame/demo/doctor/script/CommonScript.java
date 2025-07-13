package jforgame.demo.doctor.script;

import java.lang.reflect.Field;

import jforgame.demo.doctor.HotswapManager;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.player.PlayerManager;

/**
 * you can invoke any code here see {@link HotswapManager#loadJavaFile(String)}
 * for example HotswapManage.INSTANCE.loadJavaFile("CommonScript")
 *
 */
public class CommonScript {

	public CommonScript() {

		PlayerManager newMgr = new PlayerManager() {
			// replace class, u can add private fields
			private String newField = "newField";

			// replace class, u can add private methods
			private void sayHello() {
				System.err.println("add new field succ, it's " + newField);
				System.err.println("add new method succ");
			}

			// replace public method, in order to fix bug in product environment
			@Override
			public PlayerEnt load(Long playerId) throws Exception {
				sayHello();
				PlayerEnt newPlayer = new PlayerEnt();
				newPlayer.setId(playerId);
				newPlayer.setName("robot");
				return newPlayer;
			}
		};
		try {
			Field field = PlayerManager.class.getDeclaredField("instance");
			field.setAccessible(true);

            field.set(GameContext.playerManager, newMgr);

            PlayerEnt player = GameContext.playerManager.load(12345L);
			System.err.println(player.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
