package jforgame.demo.game;

import jforgame.demo.game.accout.entity.AccountManager;
import jforgame.demo.game.chat.ChatManager;
import jforgame.demo.game.gm.GmManager;
import jforgame.demo.game.login.LoginManager;
import jforgame.demo.game.player.PlayerManager;
import jforgame.demo.game.skill.SkillManager;

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
