package jforgame.demo.game.scene.message;

import com.baidu.bjf.remoting.protobuf.annotation.ProtobufClass;
import jforgame.demo.game.Modules;
import jforgame.socket.share.annotation.MessageMeta;
import jforgame.socket.share.message.Message;

@MessageMeta(module = Modules.SCENE, cmd = 1)
@ProtobufClass
public class ResPlayerEnterScene implements Message {

    /**
     * 地图id
     */
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
