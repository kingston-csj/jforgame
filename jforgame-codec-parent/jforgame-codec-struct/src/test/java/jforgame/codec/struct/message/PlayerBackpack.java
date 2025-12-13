package jforgame.codec.struct.message;

import java.util.LinkedList;
import java.util.List;

public class PlayerBackpack {

    private List<ItemVo> items = new LinkedList<>();

    public List<ItemVo> getItems() {
        return items;
    }

    public void setItems(List<ItemVo> items) {
        this.items = items;
    }
}
