package jforgame.codec.struct.message;

import java.util.HashMap;
import java.util.Map;

public class PlayerBackpack2 {

    private Map<String, ItemVo> items = new HashMap<>();


    public Map<String, ItemVo> getItems() {
        return items;
    }

    public void setItems(Map<String, ItemVo> items) {
        this.items = items;
    }
}
