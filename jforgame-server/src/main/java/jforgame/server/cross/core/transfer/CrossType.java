package jforgame.server.cross.core.transfer;

public enum CrossType {

    /**
     * 跨服竞技
     */
    PK(1),

    ;


    int type;

    CrossType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}