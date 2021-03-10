package jforgame.server.game;

import jforgame.server.game.accout.entity.AccountManager;
import jforgame.server.game.chat.ChatManager;
import jforgame.server.game.gm.GmManager;
import jforgame.server.game.login.LoginManager;
import jforgame.server.game.player.PlayerManager;
import jforgame.server.game.skill.SkillManager;

import java.util.Arrays;

/**
 * 游戏业务上下文
 * 管理game包下所有manager
 */
public class GameContext {

    public static AccountManager accountManager = new AccountManager();

    public static GmManager gmManager = new GmManager();

    public static LoginManager loginManager = new LoginManager();

    public static PlayerManager playerManager = new PlayerManager();

    public static SkillManager skillManager = new SkillManager();

    public static ChatManager chatManager;

    public static void init() {
        Class c = GameContext.class;
        Arrays.stream(c.getDeclaredFields()).forEach( f->{
            try {
              Object obj =  f.getType().newInstance();
              f.set(null, obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
