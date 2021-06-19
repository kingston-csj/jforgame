package jforgame.server.game.cross.transfer;

import javax.management.relation.Role;
import java.util.HashMap;
import java.util.Map;

public abstract class CrossTransfer {

    private static Map<Integer, CrossTransfer> transfers = new HashMap<>();

    private void init() {
        transfers.put(getCrossType(), this);
    }

    public abstract int getCrossType();

    public abstract void afterLoginCross(Role player);

    public static CrossTransfer queryTransfer(int type) {
        return transfers.get(type);
    }
}