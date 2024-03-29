package jforgame.demo.hotswap;

import jforgame.hotswap.DynamicClassLoader;
import org.junit.Test;

import java.lang.reflect.Field;

public class ReloadHotSwapTest {

    @Test
    public void test() throws Exception {
        ServicePool.playerService.say("Hi");

        System.out.println("执行热更前， 类加载器==" + ServicePool.playerService.getClass().getClassLoader());

        // 要用自定义类加载器，才能重新加载同名class文件
        DynamicClassLoader myLoader = new DynamicClassLoader("hotswap");
        // 实例化新的对象
        Class<?> newClazz = myLoader.findClass("jforgame.demo.hotswap.PlayerService");
        System.out.println("执行热更后1，类加载器==" + newClazz.getClassLoader());


        // 使用接口进行实例化
        IPlayerService newObj = (IPlayerService) newClazz.newInstance();
        // 反射注入
        Field field = ServicePool.class.getField("playerService");
        field.setAccessible(true);
        field.set(null, newObj);
        System.out.println("执行热更后2，类加载器==" + ServicePool.playerService.getClass().getClassLoader());
        // PlayerService实例被替换
        ServicePool.playerService.say("Hi");

    }

}
