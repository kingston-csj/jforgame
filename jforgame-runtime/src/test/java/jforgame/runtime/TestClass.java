package jforgame.runtime;

import jforgame.commons.JsonUtil;
import jforgame.runtime.clazz.ClassStats;

import java.util.Map;

public class TestClass {

    public static void main(String[] args) {
        Map<String, Object> result = ClassStats.showClassInfo(JsonUtil.class);
        System.out.println(JsonUtil.object2String(result));
    }
}
