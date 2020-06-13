package com.kingston.jforgame.server.game;

import com.kingston.jforgame.server.game.accout.entity.AccountManager;
import com.kingston.jforgame.server.game.chat.ChatManager;
import com.kingston.jforgame.server.game.gm.GmManager;
import com.kingston.jforgame.server.game.login.LoginManager;
import com.kingston.jforgame.server.game.player.PlayerManager;
import com.kingston.jforgame.server.game.skill.SkillManager;

/**
 * 游戏业务上下文
 * 管理game包下所有manager
 */
public class GameContext {

    private static AccountManager accountManager = new AccountManager();

    public static AccountManager getAccountManager() {
        return accountManager;
    }

    private static GmManager gmManager = new GmManager();

    public static GmManager getGmManager() {
        return gmManager;
    }

    private static LoginManager loginManager = new LoginManager();

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    private static PlayerManager playerManager = new PlayerManager();

    public static PlayerManager getPlayerManager() {
        return playerManager;
    }

    private static SkillManager skillManager = new SkillManager();

    public static SkillManager getSkillManager() {
        return skillManager;
    }

    private static ChatManager chatManager;

    public static ChatManager getChatManager() {
        return chatManager;
    }


}
