package jforgame.server.cross.core.transfer;

import javax.management.relation.Role;
import java.util.HashMap;
import java.util.Map;

public abstract class CrossTransfer {

    private static Map<Integer, CrossTransfer> transfers = new HashMap<>();

    private void init() {
        transfers.put(getCrossType(), this);
    }

    public abstract void afterLoginCross(Role player);

    public int getCrossType() {
        return CrossType.PK.getType();
    }

    public static CrossTransfer queryTransfer(int type) {
        return transfers.get(type);
    }
}