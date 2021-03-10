package jforgame.server.game.scene.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import jforgame.server.game.Modules;
import jforgame.server.game.scene.SceneDataPool;
import jforgame.socket.annotation.MessageMeta;
import jforgame.socket.message.Message;

@MessageMeta(module=Modules.SCENE, cmd=SceneDataPool.RES_ENTER_SCENE)
public class ResPlayerEnterScene extends Message {
	
	/** 地图id */
	@Protobuf(order = 1)
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
