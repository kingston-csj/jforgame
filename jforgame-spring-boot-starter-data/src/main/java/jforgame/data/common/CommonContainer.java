package jforgame.data.common;

import jforgame.data.Container;

import java.util.HashMap;
import java.util.Map;

public class CommonContainer extends Container<Integer, CommonData> {

    private Map<String, CommonData> map = new HashMap<>();

    @Override
    public void afterLoad() {
        data.forEach((k, v) -> {
            map.put(v.getKey(), v);
        });
    }

    public CommonData getConfigValueByKey(String key) {
        return map.get(key);
    }
}
