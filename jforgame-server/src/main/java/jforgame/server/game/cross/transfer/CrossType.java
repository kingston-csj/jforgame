package jforgame.server.game.cross.transfer;

public enum CrossType {

    /**
     * 跨服竞技
     */
    Moba(2),


    ;


    int type;

    CrossType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}