package com.kingston.jforgame.server.game.scene.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.jforgame.server.game.Modules;
import com.kingston.jforgame.server.game.scene.SceneDataPool;
import com.kingston.jforgame.socket.annotation.MessageMeta;
import com.kingston.jforgame.socket.message.Message;

@MessageMeta(module=Modules.SCENE, cmd=SceneDataPool.RES_ENTER_SCENE)
public class ResPlayerEnterSceneMessage extends Message {
	
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
