import jforgame.demo.hotswap.PlayerService
import jforgame.demo.hotswap.ServicePool

import java.lang.reflect.Field;

PlayerService newObj = new PlayerService() {
    private String newField = "newField";

    private void sayHello() {
        System.err.println("add new field succ, it's " + newField);
        System.err.println("add new method succ");
    }

    void say(String word) {
        sayHello();

    }
} ;
try {
    Field field = ServicePool.class.getField("playerService");
    field.setAccessible(true);
    field.set(null, newObj);
} catch (Exception e) {
    e.printStackTrace();
}