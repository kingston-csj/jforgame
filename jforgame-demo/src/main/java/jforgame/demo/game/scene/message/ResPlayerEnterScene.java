package jforgame.demo.game.scene.message;

import jforgame.demo.game.Modules;
import jforgame.demo.game.scene.SceneDataPool;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module=Modules.SCENE, cmd=SceneDataPool.RES_ENTER_SCENE)
public class ResPlayerEnterScene implements Message {
	
	/** 地图id */
	private int mapId;

	public int getMapId() {
		return mapId;
	}

	public void setMapId(int mapId) {
		this.mapId = mapId;
	}

	@Override
	public String toString() {
		return "ResPlayerEnterSceneMessage [mapId=" + mapId + "]";
	}

}
