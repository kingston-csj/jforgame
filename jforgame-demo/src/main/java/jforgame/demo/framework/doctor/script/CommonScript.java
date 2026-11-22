package jforgame.demo.framework.doctor.script;

import jforgame.demo.framework.doctor.HotswapManager;
import jforgame.demo.game.GameContext;
import jforgame.demo.game.database.user.PlayerEnt;
import jforgame.demo.game.player.PlayerManager;

import java.lang.reflect.Field;

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
			public PlayerEnt getEntity(Object playerId) {
				sayHello();
				PlayerEnt newPlayer = new PlayerEnt();
				newPlayer.setId((Long) playerId);
				newPlayer.setName("robot");
				return newPlayer;
			}
		};
		try {
			Field field = PlayerManager.class.getDeclaredField("instance");
			field.setAccessible(true);

            field.set(GameContext.playerManager, newMgr);

            PlayerEnt player = GameContext.playerManager.getEntity(12345L);
			System.err.println(player.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
