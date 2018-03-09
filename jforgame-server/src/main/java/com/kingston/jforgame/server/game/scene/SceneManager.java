package com.kingston.jforgame.server.game.scene;

public class SceneManager {

	private static volatile SceneManager instance = new SceneManager();

	public static SceneManager getInstance() {
		return instance;
	}


}
