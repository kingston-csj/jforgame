package jforgame.server.game.cross.transfer;

import javax.management.relation.Role;

public class MobaTransfer extends CrossTransfer {

    @Override
    public int getCrossType() {
        return CrossType.Moba.getType();
    }

    @Override
    public void afterLoginCross(Role player) {

    }
}
