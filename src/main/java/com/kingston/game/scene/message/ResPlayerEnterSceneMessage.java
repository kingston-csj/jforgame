package com.kingston.game.scene.message;

import com.baidu.bjf.remoting.protobuf.annotation.Protobuf;
import com.kingston.game.Modules;
import com.kingston.game.scene.SceneDataPool;
import com.kingston.net.Message;
import com.kingston.net.annotation.MessageMeta;

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
